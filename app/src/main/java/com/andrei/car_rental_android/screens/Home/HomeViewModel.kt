package com.andrei.car_rental_android.screens.Home

import android.location.Location
import android.os.CountDownTimer
import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.PaymentResponse
import com.andrei.car_rental_android.DTOs.toLocation
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.repositories.CarRepository
import com.andrei.car_rental_android.engine.repositories.PaymentRepository
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.response.ReservationRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class HomeViewModel(coroutineProvider:CoroutineScope?): BaseViewModel(coroutineProvider) {
    protected val reservationTimeSeconds:Long = 15 * 60
    protected val unlockDistance:Long = 200

    abstract val nearbyCars:StateFlow<HomeViewModelState>
    abstract val locationState:StateFlow<LocationState>
    abstract val locationRequirements:StateFlow<Set<LocationRequirement>>


    abstract val carReservationState:StateFlow<CarReservationState>
    abstract val reservationTimeLeftMillis:StateFlow<Long>

    abstract fun setLocationRequirementResolved(locationRequirement: LocationRequirement)


    abstract fun startUnlockPaymentProcess()
    abstract fun setLocationState(locationState:LocationState)
    abstract fun reserveCar(car:Car)
    abstract fun cancelReservation()

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
            object ReadyForUnlockPayment:CarReservationState()
            object LoadingPaymentData:CarReservationState()
            data class PaymentDataReady(
                val paymentResponse: PaymentResponse
            ):CarReservationState()
        }
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
}

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?,
    private val carRepository: CarRepository,
    private val paymentRepository: PaymentRepository
):HomeViewModel(coroutineProvider){

    override val nearbyCars: MutableStateFlow<HomeViewModelState> = MutableStateFlow(HomeViewModelState.Loading)
    override val locationState: MutableStateFlow<LocationState> = MutableStateFlow(LocationState.NotRequested)
    override val locationRequirements: MutableStateFlow<Set<LocationRequirement>> = MutableStateFlow(
        setOf(
            LocationRequirement.PermissionNeeded,
            LocationRequirement.LocationActive
        )
    )

    override val carReservationState: MutableStateFlow<CarReservationState> = MutableStateFlow(
        CarReservationState.Default
    )
    override val reservationTimeLeftMillis: MutableStateFlow<Long> = MutableStateFlow(reservationTimeSeconds)

    override fun setLocationRequirementResolved(locationRequirement: LocationRequirement) {
        val newSet = locationRequirements.value.toMutableSet().apply {
            remove(locationRequirement)
        }
        locationRequirements.tryEmit(newSet)
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
