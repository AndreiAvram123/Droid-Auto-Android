package com.andrei.car_rental_android.screens.Home

import android.location.Location
import android.os.CountDownTimer
import android.util.Log
import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.PaymentResponse
import com.andrei.car_rental_android.DTOs.toLocation
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.repositories.CarRepository
import com.andrei.car_rental_android.engine.repositories.DirectionsRepository
import com.andrei.car_rental_android.engine.repositories.PaymentRepository
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.response.DirectionStep
import com.andrei.car_rental_android.engine.response.ReservationRequest
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class HomeViewModel(coroutineProvider:CoroutineScope?): BaseViewModel(coroutineProvider) {
    protected val reservationTimeSeconds:Long = 15 * 60
    protected val unlockDistance:Long = 20

    abstract val nearbyCars:StateFlow<HomeViewModelState>
    abstract val locationState:StateFlow<LocationState>
    abstract val locationRequirements:StateFlow<Set<LocationRequirement>>
    abstract val directionsState:StateFlow<DirectionsState>
    abstract val rideState:StateFlow<RideState>
    abstract val cameraPosition:StateFlow<Location?>


    abstract val carReservationState:StateFlow<CarReservationState>
    abstract val reservationTimeLeftMillis:StateFlow<Long>

    abstract fun notifyRequirementResolved(locationRequirement: LocationRequirement)


    abstract fun  onFeePaymentResult(paymentResult:PaymentSheetResult)
    abstract fun startUnlockPaymentProcess()
    abstract fun setLocationState(locationState:LocationState)
    abstract fun reserveCar(car:Car)
    abstract fun cancelReservation()
    protected abstract fun unlockCar()

    sealed class HomeViewModelState{
        data class Success(val data:List<Car>):HomeViewModelState()
        object Loading:HomeViewModelState()
        object Error:HomeViewModelState()
    }

    sealed class CarReservationState{
        data class Reserved(val car:Car):CarReservationState()
        object Default:CarReservationState()
        object Error:CarReservationState()
        object InProgress:CarReservationState()

        sealed class PaymentState:CarReservationState(){
            object ReadyForUnlockPayment:PaymentState()
            object LoadingPaymentData:PaymentState()
            data class PaymentDataReady(
                val paymentResponse: PaymentResponse
            ):PaymentState()
            object PaymentFailed:PaymentState()
        }
    }
    sealed class RideState{
        object NotStarted:RideState()
        object UnlockingCar:RideState()
        object RideStarted:RideState()
    }
    sealed class LocationState{
        object NotRequested:LocationState()
        data class Resolved(val location:Location):LocationState()
        object Unknown : LocationState()
        object Loading:LocationState()
    }
    sealed class LocationRequirement{
        object PermissionNeeded:LocationRequirement()
        object LocationActive:LocationRequirement()
    }

    sealed class DirectionsState{
        data class Success(val directions:List<DirectionStep>):DirectionsState()
        object Loading : DirectionsState()
        object Error:DirectionsState()
        object Default:DirectionsState()
    }
}

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?,
    private val carRepository: CarRepository,
    private val paymentRepository: PaymentRepository,
    private val directionsRepository: DirectionsRepository
):HomeViewModel(coroutineProvider){

    override val nearbyCars: MutableStateFlow<HomeViewModelState> = MutableStateFlow(HomeViewModelState.Loading)
    override val locationState: MutableStateFlow<LocationState> = MutableStateFlow(LocationState.NotRequested)
    override val locationRequirements: MutableStateFlow<Set<LocationRequirement>> = MutableStateFlow(
        setOf(
            LocationRequirement.PermissionNeeded,
            LocationRequirement.LocationActive
        )
    )
    override val directionsState: MutableStateFlow<DirectionsState> = MutableStateFlow(DirectionsState.Default)
    override val rideState: MutableStateFlow<RideState> = MutableStateFlow(RideState.NotStarted)
    override val cameraPosition: MutableStateFlow<Location?> = MutableStateFlow(null)

    override val carReservationState: MutableStateFlow<CarReservationState> = MutableStateFlow(
        CarReservationState.Default
    )
    override val reservationTimeLeftMillis: MutableStateFlow<Long> = MutableStateFlow(reservationTimeSeconds)

    override fun notifyRequirementResolved(locationRequirement: LocationRequirement) {
        val newSet = locationRequirements.value.toMutableSet().apply {
            remove(locationRequirement)
        }
        locationRequirements.tryEmit(newSet)
    }

    override fun onFeePaymentResult(paymentResult: PaymentSheetResult) {
        when(paymentResult){
            is PaymentSheetResult.Completed -> {
                //when is done the reservation is finished and we can switch to ride
                carReservationState.tryEmit(CarReservationState.Default)
                unlockCar()
            }
            is PaymentSheetResult.Canceled -> {
                //in this case carReservationState must be data ready
                carReservationState.tryEmit(carReservationState.value)
            }
            is PaymentSheetResult.Failed -> {
                carReservationState.tryEmit(CarReservationState.PaymentState.PaymentFailed)
            }
        }
    }

    override fun unlockCar() {
        coroutineScope.launch {
            carRepository.unlockCar().collect{
                when(it){
                    is RequestState.Success -> {
                        rideState.emit(RideState.RideStarted)
                    }
                    is RequestState.Loading-> {
                        rideState.emit(RideState.UnlockingCar)
                    }
                    else -> {

                    }
                }
            }
        }
    }


    override fun startUnlockPaymentProcess() {
        coroutineScope.launch {
            paymentRepository.makeUnlockFeePayment().collect{
                when(it){
                    is RequestState.Success -> {
                        carReservationState.emit(CarReservationState.PaymentState.PaymentDataReady(it.data))
                    }
                    is RequestState.Loading ->{
                        carReservationState.emit(CarReservationState.PaymentState.LoadingPaymentData)
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private var  reservationTimer:CountDownTimer? = null


    init {
        coroutineScope.launch {
            val firstResolvedLocation =  locationState.first{state->
                state is LocationState.Resolved
            }
            if(firstResolvedLocation is LocationState.Resolved){
                getNearbyCars(firstResolvedLocation.location)
                cameraPosition.emit(firstResolvedLocation.location)
            }
        }

        coroutineScope.launch {

             carReservationState.combine(locationState){ carReservationValue,locationValue->
                if(carReservationValue is CarReservationState.Reserved && locationValue is LocationState.Resolved){
                    return@combine Pair(locationValue.location,carReservationValue.car.location.toLocation())
                }else{
                    return@combine null
                }
            }.filterNotNull().collect{locationPair->
                 Log.d("Home view model", "Getting directions")
                getDirections(
                    startLocation = locationPair.first,
                    endLocation = locationPair.second
                )
            }

        }

        coroutineScope.launch {
            combine(locationState,carReservationState){ locationState, reservationState ->
                if(locationState is LocationState.Resolved && reservationState is CarReservationState.Reserved ){
                    return@combine locationState.location.distanceTo(
                        reservationState.car.location.toLocation()
                    ).toDouble()
                }else{
                    return@combine null
                }
            }.filterNotNull().collect{ distance->
                if(distance <= unlockDistance){
                    carReservationState.emit(CarReservationState.PaymentState.ReadyForUnlockPayment)
                }
            }
        }
    }


    private suspend fun getDirections(startLocation:Location, endLocation: Location){
         directionsRepository.getDirections(
             startLocation = startLocation,
             endLocation = endLocation
         ).collect{ request->
             when(request){
                 is RequestState.Success ->{
                     directionsState.emit(DirectionsState.Success(request.data.steps))
                 }
                 is RequestState.Loading -> {
                     directionsState.emit(DirectionsState.Loading)
                 }
                 else ->{
                     directionsState.emit(DirectionsState.Error)
                 }
             }
         }
    }

    private fun startReservationTimer(){
        reservationTimer = object : CountDownTimer(
            reservationTimeSeconds * 1000,
            1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                reservationTimeLeftMillis.tryEmit(millisUntilFinished/1000L)
            }

            override fun onFinish() {
                cancelTimer()
            }

        }
        reservationTimer?.start()
    }

    private fun cancelTimer(){
        reservationTimeLeftMillis.tryEmit(reservationTimeSeconds)
        reservationTimer?.cancel()
        reservationTimer = null
    }

    private suspend fun getNearbyCars(location:Location) {
        carRepository.fetchNearby(location.latitude,location.longitude).collect {requestState->
            when(requestState){
                is RequestState.Success->{
                    nearbyCars.emit(HomeViewModelState.Success(requestState.data))
                }
                is RequestState.Loading -> {
                    nearbyCars.emit(HomeViewModelState.Loading)
                }
                else-> {
                    nearbyCars.emit(HomeViewModelState.Error)
                }
            }
        }
    }


    override fun setLocationState(locationState: LocationState) {
        this.locationState.tryEmit(locationState)
    }

    override fun reserveCar(car: Car) {
        coroutineScope.launch {
            carRepository.makeReservation(
                ReservationRequest(car.id)
            ).collect{
                when(it){
                    is RequestState.Success -> {
                        carReservationState.emit(CarReservationState.Reserved(car))
                        startReservationTimer()
                    }
                    is RequestState.Loading -> carReservationState.emit(CarReservationState.InProgress)
                    else -> carReservationState.emit(CarReservationState.Error)
                }
            }
        }
    }

    override fun cancelReservation() {
        coroutineScope.launch {
            carRepository.cancelCurrentReservation().collect{
                when(it){
                    is RequestState.Success -> {
                        cancelTimer()
                        carReservationState.emit(CarReservationState.Default)
                    }
                    is RequestState.Loading -> {

                    }
                    else -> {

                    }
                }
            }

        }
    }


}
