package com.andrei.car_rental_android.screens.Home

import android.location.Location
import android.os.CountDownTimer
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.OngoingRide
import com.andrei.car_rental_android.DTOs.Reservation
import com.andrei.car_rental_android.DTOs.toAndroidLocation
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.repositories.*
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.helpers.LocationHelper
import com.andrei.car_rental_android.screens.Home.HomeNavigator.HomeNavigationState
import com.andrei.car_rental_android.screens.Home.states.DirectionsState
import com.andrei.car_rental_android.screens.Home.states.DirectionsState.Companion.toState
import com.andrei.car_rental_android.screens.Home.states.NearbyCarsState
import com.andrei.car_rental_android.screens.Home.states.NearbyCarsState.Companion.toHomeViewModelState
import com.andrei.car_rental_android.screens.Home.states.SelectedCarState
import com.andrei.car_rental_android.screens.Home.states.UnlockPaymentState
import com.andrei.car_rental_android.screens.Home.useCases.CancelReservationUseCase
import com.andrei.car_rental_android.screens.Home.useCases.MakeReservationUseCase
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

abstract class HomeViewModel(coroutineProvider:CoroutineScope?): BaseViewModel(coroutineProvider) {
    protected val unlockDistance: Long = 300


    abstract fun checkLocationSettings(locationSettingsLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>, onLocationEnabled: () -> Unit)

    abstract val nearbyCarsState: StateFlow<NearbyCarsState>
    abstract val locationState: StateFlow<LocationState>
    abstract val locationRequirements: StateFlow<Set<LocationRequirement>>
    abstract val directionsState: StateFlow<DirectionsState>
    abstract val cameraPosition: StateFlow<Location?>
    abstract val unlockPaymentState:StateFlow<UnlockPaymentState>
    abstract val navigationState:SharedFlow<HomeNavigationState>

    abstract val selectedCarState: StateFlow<SelectedCarState>
    abstract val reservedCarLocation:StateFlow<Location?>
    abstract val reservationTimeLeft: StateFlow<Duration>

    abstract fun notifyRequirementResolved(locationRequirement: LocationRequirement)
    abstract fun  checkForReservationOrRide()


    abstract fun onFeePaymentResult(paymentResult: PaymentSheetResult)
    abstract fun startUnlockPaymentProcess()
    abstract fun reserveCar(car: Car)
    abstract fun cancelReservation()
    protected abstract fun startRide()
    abstract fun retryGetLocation()


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
    private val locationHelper: LocationHelper,
    private val reservationRepository: ReservationRepository,
    private val rideRepository: RideRepository
):HomeViewModel(coroutineProvider){

    override fun checkLocationSettings(
        locationSettingsLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
        onLocationEnabled: () -> Unit
    ) {
        coroutineScope.launch {
            locationHelper.checkLocationSettings(locationSettingsLauncher,onLocationEnabled)
        }
    }

    //todo
    //make sure that when location cannot be found appropriate error state is added

    override val nearbyCarsState: MutableStateFlow<NearbyCarsState> = MutableStateFlow(NearbyCarsState.Default)

    override val locationState: MutableStateFlow<LocationState> = MutableStateFlow(LocationState.NotRequested)

    override val locationRequirements: MutableStateFlow<Set<LocationRequirement>> = MutableStateFlow(
        setOf(
            LocationRequirement.PermissionNeeded,
            LocationRequirement.LocationActive
        )
    )
    override val directionsState: MutableStateFlow<DirectionsState> = MutableStateFlow(DirectionsState.Default)
    override val cameraPosition: MutableStateFlow<Location?> = MutableStateFlow(null)
    override val unlockPaymentState: MutableStateFlow<UnlockPaymentState> = MutableStateFlow(UnlockPaymentState.Default)
    override val navigationState: MutableSharedFlow<HomeNavigationState> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val selectedCarState: MutableStateFlow<SelectedCarState> = MutableStateFlow(
        SelectedCarState.Default
    )
    override val reservedCarLocation: MutableStateFlow<Location?> = MutableStateFlow(null)
    override val reservationTimeLeft: MutableStateFlow<Duration> =  MutableStateFlow(0.seconds)

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
                startRide()
            }
            is PaymentSheetResult.Canceled -> {
                //in this case carReservationState must be data ready
                selectedCarState.tryEmit(selectedCarState.value)
            }
            is PaymentSheetResult.Failed -> {
                unlockPaymentState.tryEmit(UnlockPaymentState.UnlockPaymentFailed)
            }
        }
    }



    override fun startRide() {
        coroutineScope.launch {
            rideRepository.startRide().collect{
                when(it){
                    is RequestState.Success -> {
                        navigationState.emit(HomeNavigationState.NavigateToRideScreen)
                    }
                    is RequestState.Loading-> {
                        selectedCarState.emit(SelectedCarState.UnlockingCar)
                    }
                    else -> {
                        //no action
                    }
                }
            }
        }
    }

    override fun retryGetLocation() {
        coroutineScope.launch {
            getLastKnownLocation()
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
        var fistLocationJob: Job? = null

        coroutineScope.launch {
            locationRequirements.collect{
                if(it.isEmpty()){
                    getLastKnownLocation()
                }
            }
        }

        fistLocationJob = coroutineScope.launch {
             locationState.collect{
                 when(it){
                     is LocationState.Resolved -> {
                         cameraPosition.emit(it.location)
                         getNearbyCars(it.location)
                         fistLocationJob?.cancel()
                     }
                     LocationState.Unknown -> {
                         nearbyCarsState.emit(NearbyCarsState.ErrorUnknownLocation)
                     }
                     else ->{
                         //no action
                     }
                 }
            }
        }
        coroutineScope.launch {
            selectedCarState.collect{
                when(it){
                    is SelectedCarState.Default ->{
                        unlockPaymentState.emit(UnlockPaymentState.Default)
                        cancelTimer()
                    }
                    is SelectedCarState.Reserved ->{
                        startReservationTimer(it.remainingTime)
                    }
                }

            }
        }
        coroutineScope.launch {
            combine(reservedCarLocation,locationState){ carLocationValue, locationValue->
                if(carLocationValue != null  && locationValue is LocationState.Resolved){
                    return@combine Pair(locationValue.location,carLocationValue)
                }else{
                    return@combine null
                }
            }.filterNotNull().collectLatest{ locationPair->
                if(locationPair.first.distanceTo(
                        locationPair.second
                    ) <= unlockDistance)  {
                    unlockPaymentState.emit(UnlockPaymentState.ReadyForUnlockUnlockPayment)
                }else{
                    getDirections(
                        startLocation = locationPair.first,
                        endLocation = locationPair.second
                    )
                }
            }
        }

    }



    //todo
    //this might need to be moved into the splash screen to check for an ongoing ride
    override fun checkForReservationOrRide(){
        coroutineScope.launch {
            combine(
                reservationRepository.getCurrentReservation(),
                rideRepository.getOngoingRide()
            ){ requestReservation, requestRide ->
                when{
                    requestReservation is RequestState.Success && requestReservation.data != null -> {
                        return@combine  requestReservation
                    }
                    requestRide is RequestState.Success && requestRide.data != null -> {
                        return@combine requestRide
                    }
                    requestReservation is RequestState.Loading || requestRide is RequestState.Loading->{
                        return@combine RequestState.Loading
                    }

                    else -> return@combine RequestState.Success(null)
                }

            }.collect{
                when(it){
                    is RequestState.Success -> {
                        when (it.data) {
                            is Reservation -> {
                                selectedCarState.emit(
                                    SelectedCarState.Reserved(
                                        car = it.data.car,
                                        remainingTime = it.data.remainingTime.seconds
                                    )
                                )

                                getReservedCarLocation(it.data.car)
                            }
                            is OngoingRide -> {
                                navigationState.emit(
                                    HomeNavigationState.NavigateToRideScreen
                                )
                            }
                        }
                    }
                    is RequestState.Loading->{
                        //todo
                        //add some loading state
                    }
                    else ->{
                        //no action
                    }
                }

            }

        }
    }

    private suspend fun getReservedCarLocation(car:Car){
        carRepository.getCarLocation(car).collect{
            when(it){
                is RequestState.Success -> {
                    if(it.data != null){
                        reservedCarLocation.emit(it.data.toAndroidLocation())
                    }
                }
                is RequestState.Loading -> {

                }
                else ->{}

            }
        }
    }


    private suspend fun getDirections(startLocation:Location, endLocation: Location){
        directionsRepository.getDirections(
            startLocation = startLocation,
            endLocation = endLocation
        ).collect{ directionsState.emit(it.toState())}

    }

    private fun startReservationTimer(duration:Duration){
        reservationTimer = object : CountDownTimer(
            duration.inWholeMilliseconds,
            1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                reservationTimeLeft.tryEmit(millisUntilFinished.toDuration(DurationUnit.MILLISECONDS))
            }

            override fun onFinish() {
                //the reservation will cancel automatically on the server side
                selectedCarState.tryEmit(SelectedCarState.Default)
                cancelTimer()
            }

        }
        reservationTimer?.start()
    }

    private fun cancelTimer(){
        reservationTimer?.cancel()
        reservationTimer = null
    }

    private suspend fun getNearbyCars(location:Location) {
        carRepository.getNearbyCars(location).collect {
            nearbyCarsState.emit(it.toHomeViewModelState())
        }

    }


    override fun reserveCar(car: Car) {
        coroutineScope.launch {
            makeReservationUseCase(car).collect{
                if(it is SelectedCarState.Reserved){
                    reservedCarLocation.emit(
                        getCarLocationOnMap(car)
                    )
                }
                selectedCarState.emit(it)
            }
        }
    }

    private fun getCarLocationOnMap(car:Car):Location?{
        val nearbyCarsStateValue = nearbyCarsState.value
        if(nearbyCarsStateValue is NearbyCarsState.Success){
            val nearbyCars = nearbyCarsStateValue.data
            return nearbyCars.find { it.car == car }?.location?.toAndroidLocation()
        }
        return null
    }

    override fun cancelReservation() {
        coroutineScope.launch {
            cancelReservationUseCase().collect{
                selectedCarState.emit(it)
                reservedCarLocation.emit(null)
            }
        }
    }


}
