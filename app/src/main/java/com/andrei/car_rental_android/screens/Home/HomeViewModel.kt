package com.andrei.car_rental_android.screens.Home

import android.location.Location
import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.OngoingRide
import com.andrei.car_rental_android.DTOs.Reservation
import com.andrei.car_rental_android.DTOs.toLocation
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.repositories.*
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.helpers.LocationHelper
import com.andrei.car_rental_android.screens.Home.states.*
import com.andrei.car_rental_android.screens.Home.states.DirectionsState.Companion.toState
import com.andrei.car_rental_android.screens.Home.states.HomeViewModelState.Companion.toHomeViewModelState
import com.andrei.car_rental_android.screens.Home.useCases.CancelReservationUseCase
import com.andrei.car_rental_android.screens.Home.useCases.MakeReservationUseCase
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
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

    abstract val nearbyCars: StateFlow<HomeViewModelState>
    abstract val locationState: StateFlow<LocationState>
    abstract val locationRequirements: StateFlow<Set<LocationRequirement>>
    abstract val directionsState: StateFlow<DirectionsState>
    abstract val cameraPosition: StateFlow<Location?>
    abstract val unlockPaymentState:StateFlow<UnlockPaymentState>
    abstract val navigationState:StateFlow<HomeNavigationState>

    abstract val selectedCarState: StateFlow<SelectedCarState>
    abstract val reservationTimeLeftText: StateFlow<String>

    abstract fun notifyRequirementResolved(locationRequirement: LocationRequirement)
   abstract fun  checkForReservationOrRide()


    abstract fun onFeePaymentResult(paymentResult: PaymentSheetResult)
    abstract fun startUnlockPaymentProcess()
    abstract fun setLocationState(locationState: LocationState)
    abstract fun reserveCar(car: Car)
    abstract fun cancelReservation()
    protected abstract fun unlockCar()


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
    override val cameraPosition: MutableStateFlow<Location?> = MutableStateFlow(null)
    override val unlockPaymentState: MutableStateFlow<UnlockPaymentState> = MutableStateFlow(UnlockPaymentState.Default)
    override val navigationState: MutableStateFlow<HomeNavigationState> = MutableStateFlow(HomeNavigationState.Default)


    override val selectedCarState: MutableStateFlow<SelectedCarState> = MutableStateFlow(
        SelectedCarState.Default
    )

    override val reservationTimeLeftText: MutableStateFlow<String> = MutableStateFlow("")

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
                unlockCar()
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



    override fun unlockCar() {
        coroutineScope.launch {
            //when is done the reservation is finished and we can switch to ride
            carRepository.unlockCar().collect{
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
        checkForReservationOrRide()
        coroutineScope.launch {
            reservationRepository.getCurrentReservation().collect{ state->
                when(state){
                    is RequestState.Success ->{
                        state.data?.let {reservation ->
                            selectedCarState.emit(SelectedCarState.Reserved(
                                car = reservation.car,
                                remainingTime = reservation.remainingTime.seconds
                            ))
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
            selectedCarState.collect{
                when(it){
                    is SelectedCarState.Default ->{
                        unlockPaymentState.emit(UnlockPaymentState.Default)
                        reservationTimeLeftText.tryEmit("")
                        cancelTimer()
                    }
                    is SelectedCarState.Reserved ->{
                        startReservationTimer(it.remainingTime)
                    }
                }

            }
        }
        coroutineScope.launch {
               combine(selectedCarState,locationState){ carReservationValue, locationValue->
                if(carReservationValue is SelectedCarState.Reserved && locationValue is LocationState.Resolved){
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
            combine(locationState,selectedCarState){ locationState, reservationState ->
                if(locationState is LocationState.Resolved && reservationState is SelectedCarState.Reserved ){
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
                    is RequestState.Success ->{
                        if(it.data != null){
                            if (it.data is Reservation){
                                selectedCarState.emit(
                                    SelectedCarState.Reserved(
                                        car = it.data.car,
                                        remainingTime = it.data.remainingTime.toDuration(DurationUnit.SECONDS)
                                    )
                                )
                            }
                            if(it.data is OngoingRide){
                                navigationState.emit(
                                    HomeNavigationState.NavigateToRideScreen
                                )
                            }
                        }else{
                            //no ride and no reservation
                            //finsish loading
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
               reservationTimeLeftText.tryEmit(
                   DateUtils.formatElapsedTime(millisUntilFinished/1000L)
               )
            }

            override fun onFinish() {
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
                 selectedCarState.emit(it)
             }
        }
    }

    override fun cancelReservation() {
        coroutineScope.launch {
            cancelReservationUseCase().collect{
                selectedCarState.emit(it)
            }
        }
    }


}
