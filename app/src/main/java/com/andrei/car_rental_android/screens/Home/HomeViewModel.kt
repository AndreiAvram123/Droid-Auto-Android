package com.andrei.car_rental_android.screens.Home

import android.location.Location
import android.os.CountDownTimer
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.toLocation
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.repositories.CarRepository
import com.andrei.car_rental_android.engine.repositories.DirectionsRepository
import com.andrei.car_rental_android.engine.repositories.PaymentRepository
import com.andrei.car_rental_android.engine.repositories.ReservationRepository
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.helpers.LocationHelper
import com.andrei.car_rental_android.screens.Home.states.CarReservationState
import com.andrei.car_rental_android.screens.Home.states.DirectionsState
import com.andrei.car_rental_android.screens.Home.states.DirectionsState.Companion.toState
import com.andrei.car_rental_android.screens.Home.states.HomeViewModelState
import com.andrei.car_rental_android.screens.Home.states.HomeViewModelState.Companion.toHomeViewModelState
import com.andrei.car_rental_android.screens.Home.states.UnlockPaymentState
import com.andrei.car_rental_android.screens.Home.useCases.CancelReservationUseCase
import com.andrei.car_rental_android.screens.Home.useCases.FormatTimeUseCase
import com.andrei.car_rental_android.screens.Home.useCases.MakeReservationUseCase
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

abstract class HomeViewModel(coroutineProvider:CoroutineScope?): BaseViewModel(coroutineProvider) {
    protected val reservationTime: Duration = (15 * 60).seconds
    protected val unlockDistance: Long = 200

    abstract fun checkLocationSettings(locationSettingsLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>, onLocationEnabled: () -> Unit)

    abstract val nearbyCars: StateFlow<HomeViewModelState>
    abstract val locationState: StateFlow<LocationState>
    abstract val locationRequirements: StateFlow<Set<LocationRequirement>>
    abstract val directionsState: StateFlow<DirectionsState>
    abstract val rideState: StateFlow<RideState>
    abstract val cameraPosition: StateFlow<Location?>
    abstract val unlockPaymentState:StateFlow<UnlockPaymentState>

    abstract val carReservationState: StateFlow<CarReservationState>
    abstract val reservationTimeLeftText: StateFlow<String>

    abstract fun notifyRequirementResolved(locationRequirement: LocationRequirement)


    abstract fun onFeePaymentResult(paymentResult: PaymentSheetResult)
    abstract fun startUnlockPaymentProcess()
    abstract fun setLocationState(locationState: LocationState)
    abstract fun reserveCar(car: Car)
    abstract fun cancelReservation()
    protected abstract fun unlockCar()


    sealed class RideState {
        object NotStarted : RideState()
        object UnlockingCar : RideState()
        object RideStarted : RideState()
    }

    sealed class LocationState {
        object NotRequested : LocationState()
        data class Resolved(val location: Location) : LocationState()
        object Unknown : LocationState()
        object Loading : LocationState()
    }

    sealed class LocationRequirement {
        object PermissionNeeded : LocationRequirement()
        object LocationActive : LocationRequirement()
    }

}
@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?,
    private val carRepository: CarRepository,
    private val paymentRepository: PaymentRepository,
    private val directionsRepository: DirectionsRepository,
    private val makeReservationUseCase: MakeReservationUseCase,
    private val cancelReservationUseCase: CancelReservationUseCase,
    private val formatTimeUseCase: FormatTimeUseCase,
    private val locationHelper: LocationHelper,
    private val reservationRepository: ReservationRepository
):HomeViewModel(coroutineProvider){

    override fun checkLocationSettings(
        locationSettingsLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
        onLocationEnabled: () -> Unit
    ) = locationHelper.checkLocationSettings(locationSettingsLauncher,onLocationEnabled)

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
    override val unlockPaymentState: MutableStateFlow<UnlockPaymentState> = MutableStateFlow(UnlockPaymentState.Default)

    override val carReservationState: MutableStateFlow<CarReservationState> = MutableStateFlow(
        CarReservationState.Default
    )
    private val reservationTimeLeft:MutableStateFlow<Duration> = MutableStateFlow(reservationTime)
    override val reservationTimeLeftText: StateFlow<String> = reservationTimeLeft.transform {
        emit(formatTimeUseCase(it))
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = formatTimeUseCase(reservationTime)
    )

        override fun notifyRequirementResolved(locationRequirement: LocationRequirement) {
        val newSet = locationRequirements.value.toMutableSet().apply {
            remove(locationRequirement)
        }
        locationRequirements.tryEmit(newSet)
    }

    override fun onCleared() {
        locationHelper.stopLocationUpdates()
    }

    override fun onFeePaymentResult(paymentResult: PaymentSheetResult) {
        when(paymentResult){
            is PaymentSheetResult.Completed -> {
                //when is done the reservation is finished and we can switch to ride
                carReservationState.tryEmit(CarReservationState.FullyReserved)
                unlockCar()
            }
            is PaymentSheetResult.Canceled -> {
                //in this case carReservationState must be data ready
                carReservationState.tryEmit(carReservationState.value)
            }
            is PaymentSheetResult.Failed -> {
                unlockPaymentState.tryEmit(UnlockPaymentState.UnlockPaymentFailed)
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
                        unlockPaymentState.emit(UnlockPaymentState.UnlockPaymentDataReady(it.data))
                    }
                    is RequestState.Loading ->{
                        unlockPaymentState.emit(UnlockPaymentState.LoadingUnlockPaymentData)
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private var  reservationTimer:CountDownTimer? = null

    private suspend fun getLastKnownLocation(){
        locationState.emit(LocationState.Loading)
        val location = locationHelper.getLastKnownLocation()
        if(location != null){
            locationState.emit(LocationState.Resolved(location))
            locationHelper.requestLocationUpdates(LocationHelper.highPrecisionHighIntervalRequest)
        }else{
            locationState.emit(LocationState.Unknown)
        }
    }

    init {
        coroutineScope.launch {
            reservationRepository.getCurrentReservation().collect{ state->
                when(state){
                    is RequestState.Success ->{
                        val reservation = state.data?.temporaryReservation
                        if(reservation != null){
                            carReservationState.emit(CarReservationState.TemporaryReserved(
                                car = reservation.car
                            ))
                            reservationTimeLeft.emit(reservation.remainingTime.seconds)
                        }
                    }
                    is  RequestState.Loading -> {

                    }
                    else ->{

                    }
                }
            }
        }
        
        coroutineScope.launch {
            locationRequirements.collect{
                if(it.isEmpty()){
                    getLastKnownLocation()
                }
            }
        }

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
            carReservationState.collect{
                when(it){
                    is CarReservationState.Default ->{
                        cancelTimer()
                    }
                    is CarReservationState.TemporaryReserved ->{
                        startReservationTimer()
                    }
                }

            }
        }

        coroutineScope.launch {
               combine(carReservationState,locationState){ carReservationValue,locationValue->
                if(carReservationValue is CarReservationState.TemporaryReserved && locationValue is LocationState.Resolved){
                    return@combine Pair(locationValue.location,carReservationValue.car.location.toLocation())
                }else{
                    return@combine null
                }
            }.filterNotNull().collectLatest{ locationPair->
                getDirections(
                    startLocation = locationPair.first,
                    endLocation = locationPair.second
                )
            }
        }

        coroutineScope.launch {
            combine(locationState,carReservationState){ locationState, reservationState ->
                if(locationState is LocationState.Resolved && reservationState is CarReservationState.TemporaryReserved ){
                    return@combine locationState.location.distanceTo(
                        reservationState.car.location.toLocation()
                    ).toDouble()
                }else{
                    return@combine null
                }
            }.filterNotNull().collect{ distance->
                if(distance <= unlockDistance){
                    unlockPaymentState.emit(UnlockPaymentState.ReadyForUnlockUnlockPayment)
                }
            }
        }
    }


    private suspend fun getDirections(startLocation:Location, endLocation: Location){
         directionsRepository.getDirections(
             startLocation = startLocation,
             endLocation = endLocation
         ).collect{ directionsState.emit(it.toState())}

    }

    private fun startReservationTimer(){
        reservationTimer = object : CountDownTimer(
            reservationTimeLeft.value.inWholeMilliseconds,
            1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                reservationTimeLeft.tryEmit(millisUntilFinished.milliseconds)
            }

            override fun onFinish() {
                cancelTimer()
            }

        }
        reservationTimer?.start()
    }

    private fun cancelTimer(){
        reservationTimeLeft.tryEmit(reservationTime)
        reservationTimer?.cancel()
        reservationTimer = null
    }

    private suspend fun getNearbyCars(location:Location) {
        carRepository.fetchNearby(location.latitude,location.longitude).collect {
           nearbyCars.emit(it.toHomeViewModelState())
        }
    }


    override fun setLocationState(locationState: LocationState) {
        this.locationState.tryEmit(locationState)
    }

    override fun reserveCar(car: Car) {
        coroutineScope.launch {
             makeReservationUseCase(car).collect{
                 carReservationState.emit(it)
             }
        }
    }

    override fun cancelReservation() {
        coroutineScope.launch {
            cancelReservationUseCase().collect{
                carReservationState.emit(it)
            }
        }
    }


}
