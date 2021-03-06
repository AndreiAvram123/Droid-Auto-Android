package com.andrei.car_rental_android.screens.ride

import com.andrei.car_rental_android.DTOs.FinishedRide
import com.andrei.car_rental_android.DTOs.OngoingRide
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.repositories.CarRepository
import com.andrei.car_rental_android.engine.repositories.RideRepository
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.ui.utils.unixTimeSeconds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timerTask
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

abstract class RideViewModel(coroutineProvider: CoroutineScope?): BaseViewModel(coroutineProvider) {

    abstract fun finishRide()
    abstract fun lockCar()
    abstract fun unlockCar()

    abstract fun getOngoingRide()
    abstract val currentRideState:StateFlow<RideState>
    abstract val elapsedTime:StateFlow<Duration>
    abstract val carLocked:StateFlow<Boolean>

    sealed class RideState{
        object Loading:RideState()
        data class Success(val ongoingRide: OngoingRide):RideState()
        data class Error(val message:String):RideState()
        object FinishingRide:RideState()
        data class RideFinished(val finishedRide: FinishedRide):RideState()
        data class ErrorFinishingRide(val message: String):RideState()
    }



    companion object{
        fun  RequestState<OngoingRide?>.toRideState(): RideState{
            return when(this){
                is RequestState.Success -> {
                    RideState.Success(this.data!!)
                }
                is RequestState.Loading -> {
                    RideState.Loading
                }
                is RequestState.Error->{
                    RideState.Error(this.message)
                }
                else ->{
                    RideState.Error("That's on our side... We are having problems communicating with your device")
                }


            }
        }

    }
    
}

@HiltViewModel
class RideViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?,
    private val rideRepository: RideRepository,
    private val carRepository: CarRepository
): RideViewModel(coroutineProvider){

    private var timer:Timer? = null


    override val currentRideState: MutableStateFlow<RideState> = MutableStateFlow(RideState.Loading)
    override val elapsedTime: MutableStateFlow<Duration> = MutableStateFlow(0.seconds)
    override val carLocked: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val incrementTask = timerTask {
        elapsedTime.tryEmit(elapsedTime.value + 1.seconds)
    }

    init {

        coroutineScope.launch {
            currentRideState.collect{ rideState->
                if(rideState is RideState.Success){
                     val elapsedSeconds  = unixTimeSeconds()-  rideState.ongoingRide.startTime
                     this@RideViewModelImpl.elapsedTime.emit(elapsedSeconds.toDuration(
                         DurationUnit.SECONDS
                     ))
                     startTimer()
                }
            }

        }
    }

    private fun startTimer(){
        timer = Timer().also {
            it.scheduleAtFixedRate(incrementTask,0,1000)
        }
    }

    override fun finishRide() {
        coroutineScope.launch {
           rideRepository.finishOngoingRide().collect{requestState->
               when(requestState){
                   is RequestState.Success -> {
                       currentRideState.emit(RideState.RideFinished(requestState.data))
                   }
                   is RequestState.Loading -> {
                      currentRideState.emit(RideState.FinishingRide)
                   }
                  is RequestState.Error->{

                  }
                  is RequestState.ConnectionError -> {

                  }
               }
           }
        }
    }

    override fun lockCar() {
        coroutineScope.launch {
            carRepository.lockCar().collect {
                if (it is RequestState.Success) {
                    carLocked.emit(true)
                }
            }
        }
    }

    override fun unlockCar() {
        coroutineScope.launch {
            carRepository.unlockCar().collect {
                if (it is RequestState.Success) {
                    carLocked.emit(false)
                }
            }
        }
    }

    override fun getOngoingRide() {
        coroutineScope.launch {
           rideRepository.getOngoingRide().collect{
               currentRideState.emit(it.toRideState())
           }
        }
    }

}