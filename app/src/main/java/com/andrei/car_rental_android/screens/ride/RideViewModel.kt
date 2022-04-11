package com.andrei.car_rental_android.screens.ride

import com.andrei.car_rental_android.DTOs.OngoingRide
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.repositories.RideRepository
import com.andrei.car_rental_android.engine.request.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timerTask

abstract class RideViewModel(coroutineProvider: CoroutineScope?): BaseViewModel(coroutineProvider) {

    abstract fun finishRide()
    abstract fun getOngoingRide()
    abstract val currentRideState:StateFlow<RideState>
    abstract val elapsedSeconds:StateFlow<Long>
    abstract val rideCost:StateFlow<Double>
    abstract val finishRideResult:StateFlow<FinishRideResult>

    sealed class RideState{
        data class Success(val ongoingRide: OngoingRide):RideState()
        object Loading:RideState()
        data class Error(val message:String):RideState()
    }

    sealed class FinishRideResult{
        object Default:FinishRideResult()
        object Loading:FinishRideResult()
        object Success:FinishRideResult()
        object Error:FinishRideResult()
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
): RideViewModel(coroutineProvider){

    private var timer:Timer? = null


    override val currentRideState: MutableStateFlow<RideState> = MutableStateFlow(RideState.Loading)
    override val elapsedSeconds: MutableStateFlow<Long> = MutableStateFlow(0)
    override val rideCost: StateFlow<Double>  =  elapsedSeconds.transform {
        emit((it/60) * pricePerMinute)
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = 0.0
    )
    override val finishRideResult: MutableStateFlow<FinishRideResult> = MutableStateFlow(FinishRideResult.Default)


    private var pricePerMinute:Double= 0.0

    private val incrementTask = timerTask {
        elapsedSeconds.tryEmit(elapsedSeconds.value + 1)
    }

    init {

        coroutineScope.launch {
            currentRideState.collect{ rideState->
                if(rideState is RideState.Success){
                     pricePerMinute = rideState.ongoingRide.car.pricePerMinute
                     val elapsedTime  = System.currentTimeMillis()/1000L - rideState.ongoingRide.startedTime
                     elapsedSeconds.emit(elapsedTime)
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
                       finishRideResult.emit(FinishRideResult.Success)
                   }
                   is RequestState.Loading -> {

                   }
                  is RequestState.Error->{

                  }
                  is RequestState.ConnectionError -> {

                  }
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