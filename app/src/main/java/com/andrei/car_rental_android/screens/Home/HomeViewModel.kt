package com.andrei.car_rental_android.screens.Home

import android.location.Location
import android.os.CountDownTimer
import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.repositories.CarRepository
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.response.ReservationRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class HomeViewModel(coroutineProvider:CoroutineScope?): BaseViewModel(coroutineProvider) {
    abstract val nearbyCars:StateFlow<HomeViewModelState>
    abstract val locationState:StateFlow<LocationState>
    abstract val currentLocationRequirement:StateFlow<LocationRequirement>
    abstract val reservationState:StateFlow<ReservationState>
    abstract val reservationTimeLeftMillis:StateFlow<Long>

    val reservationTimeSeconds:Long = 15 * 60

    abstract fun setLocation(location: Location)
    abstract fun setLocationUnknown()
    abstract fun reserveCar(car:Car)
    abstract fun cancelReservation()

    sealed class HomeViewModelState{
        data class Success(val data:List<Car>):HomeViewModelState()
        object Loading:HomeViewModelState()
        object Error:HomeViewModelState()
    }

    sealed class ReservationState{
        data class Reserved(val car:Car):ReservationState()
        object Default:ReservationState()
        object Error:ReservationState()
        object InProgress:ReservationState()
    }
    sealed class LocationState{
        data class Determined(val location:Location):LocationState()
        object Unknown : LocationState()
        object Loading:LocationState()
    }
    sealed class LocationRequirement{
        object None :LocationRequirement()
        object PermissionNeeded:LocationRequirement()
        object LocationActive:LocationRequirement()
    }
}

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?,
    private val carRepository: CarRepository
):HomeViewModel(coroutineProvider){

    override val nearbyCars: MutableStateFlow<HomeViewModelState> = MutableStateFlow(HomeViewModelState.Loading)
    override val locationState: MutableStateFlow<LocationState> = MutableStateFlow(LocationState.Loading)
    override val currentLocationRequirement: MutableStateFlow<LocationRequirement> = MutableStateFlow(LocationRequirement.PermissionNeeded)

    override val reservationState: MutableStateFlow<ReservationState> = MutableStateFlow(
        ReservationState.Default
    )
    override val reservationTimeLeftMillis: MutableStateFlow<Long> = MutableStateFlow(reservationTimeSeconds)

    private var  reservationTimer:CountDownTimer? = null
    
    
    init {
        coroutineScope.launch {
            locationState.collect{state->
                if(state is LocationState.Determined){
                    getNearbyCars(state.location)
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

    override fun setLocation(location: Location) {
        locationState.tryEmit(LocationState.Determined(location))
    }

    override fun setLocationUnknown() {
        locationState.tryEmit(LocationState.Unknown)
    }

    override fun reserveCar(car: Car) {
         coroutineScope.launch {
             carRepository.makeReservation(
                 ReservationRequest(car.id)
             ).collect{
                 when(it){
                     is RequestState.Success -> {
                         reservationState.emit(ReservationState.Reserved(car))
                         startReservationTimer()
                     }
                     is RequestState.Loading -> reservationState.emit(ReservationState.InProgress)
                     else -> reservationState.emit(ReservationState.Error)
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
                        reservationState.emit(ReservationState.Default)
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
