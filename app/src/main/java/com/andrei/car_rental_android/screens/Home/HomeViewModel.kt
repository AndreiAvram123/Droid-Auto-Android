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
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.helpers.LocationHelper
import com.andrei.car_rental_android.screens.Home.states.CarReservationState
import com.andrei.car_rental_android.screens.Home.states.DirectionsState
import com.andrei.car_rental_android.screens.Home.states.DirectionsState.Companion.toState
import com.andrei.car_rental_android.screens.Home.states.HomeViewModelState
import com.andrei.car_rental_android.screens.Home.states.HomeViewModelState.Companion.toHomeViewModelState
import com.andrei.car_rental_android.screens.Home.states.PaymentState
import com.andrei.car_rental_android.screens.Home.useCases.CancelReservationUseCase
import com.andrei.car_rental_android.screens.Home.useCases.FormatTimeUseCase
import com.andrei.car_rental_android.screens.Home.useCases.MakeReservationUseCase
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

abstract class HomeViewModel(coroutineProvider:CoroutineScope?): BaseViewModel(coroutineProvider) {
    protected val reservationTimeSeconds: Long = 15 * 60
    protected val unlockDistance: Long = 200

    abstract fun checkLocationSettings(locationSettingsLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>, onLocationEnabled: () -> Unit)

    abstract val nearbyCars: StateFlow<HomeViewModelState>
    abstract val locationState: StateFlow<LocationState>
    abstract val locationRequirements: StateFlow<Set<LocationRequirement>>
    abstract val directionsState: StateFlow<DirectionsState>
    abstract val rideState: StateFlow<RideState>
    abstract val cameraPosition: StateFlow<Location?>

    abstract val carReservationState: StateFlow<CarReservationState>
    abstract val reservationTimeLeft: StateFlow<String>

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
    private val locationHelper: LocationHelper
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

    override val carReservationState: MutableStateFlow<CarReservationState> = MutableStateFlow(
        CarReservationState.Default
    )
    override val reservationTimeLeft: MutableStateFlow<String> = MutableStateFlow(
        formatTimeUseCase(reservationTimeSeconds.seconds)
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
                carReservationState.tryEmit(CarReservationState.Default)
                unlockCar()
            }
            is PaymentSheetResult.Canceled -> {
                //in this case carReservationState must be data ready
                carReservationState.tryEmit(carReservationState.value)
            }
            is PaymentSheetResult.Failed -> {
                carReservationState.tryEmit(PaymentState.PaymentFailed)
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
                        carReservationState.emit(PaymentState.PaymentDataReady(it.data))
                    }
                    is RequestState.Loading ->{
                        carReservationState.emit(PaymentState.LoadingPaymentData)
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
            locationRequirements.collect{
                if(it.isEmpty()){
                    val location = locationHelper.getLastKnownLocation()
                    if(location != null){
                        locationState.emit(LocationState.Resolved(location))
                        locationHelper.requestLocationUpdates(LocationHelper.highPrecisionHighIntervalRequest)
                    }else{
                        locationState.emit(LocationState.Unknown)
                    }
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
                if(it is CarReservationState.Default){
                    cancelTimer()
                }
            }
        }

        coroutineScope.launch {
               combine(carReservationState,locationState){ carReservationValue,locationValue->
                if(carReservationValue is CarReservationState.PreReserved && locationValue is LocationState.Resolved){
                    return@combine Pair(locationValue.location,carReservationValue.car.location.toLocation())
                }else{
                    return@combine null
                }
            }.filterNotNull().collectLatest{locationPair->
                Timber.d("Getting directions")
                getDirections(
                    startLocation = locationPair.first,
                    endLocation = locationPair.second
                )
            }
        }

        coroutineScope.launch {
            combine(locationState,carReservationState){ locationState, reservationState ->
                if(locationState is LocationState.Resolved && reservationState is CarReservationState.PreReserved ){
                    return@combine locationState.location.distanceTo(
                        reservationState.car.location.toLocation()
                    ).toDouble()
                }else{
                    return@combine null
                }
            }.filterNotNull().collect{ distance->
                if(distance <= unlockDistance){
                    carReservationState.emit(PaymentState.ReadyForUnlockPayment)
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
            reservationTimeSeconds * 1000,
            1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                reservationTimeLeft.tryEmit(formatTimeUseCase(millisUntilFinished.milliseconds))
            }

            override fun onFinish() {
                cancelTimer()
            }

        }
        reservationTimer?.start()
    }

    private fun cancelTimer(){
        reservationTimeLeft.tryEmit(formatTimeUseCase(reservationTimeSeconds.seconds))
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
                 if(it is CarReservationState.PreReserved){
                     startReservationTimer()
                 }
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
