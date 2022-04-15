package com.andrei.car_rental_android.screens.finishedRide

import androidx.lifecycle.SavedStateHandle
import com.andrei.car_rental_android.DTOs.FinishedRide
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.repositories.RideRepository
import com.andrei.car_rental_android.engine.request.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class FinishedRideViewModel(coroutineProvider:CoroutineScope?) : BaseViewModel(coroutineProvider) {
  abstract fun getReceipt()
  abstract val rideState:StateFlow<ScreenState>

  sealed class ScreenState{
      data class Success(val finishedRide: FinishedRide):ScreenState()
      object Loading:ScreenState()
      object Error:ScreenState()
  }
}

@HiltViewModel
class FinishedRideViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?,
    savedStateHandle: SavedStateHandle,
    private val rideRepository: RideRepository
): FinishedRideViewModel(coroutineProvider) {

    private val args:FinishedRideNavHelper.Args  = FinishedRideNavHelper.parseArguments(
        savedStateHandle
    )
    override val rideState: MutableStateFlow<ScreenState> = MutableStateFlow(ScreenState.Loading)

    override fun getReceipt() {
       coroutineScope.launch {
           rideRepository.getRideByID(args.rideID).collect{requestState->
               when(requestState){
                   is RequestState.Success -> {
                       rideState.emit(ScreenState.Success(requestState.data))
                   }
                   is RequestState.Loading -> {
                       rideState.emit(ScreenState.Loading)
                   }
                   else ->{
                       rideState.emit(ScreenState.Error)
                   }
               }
           }
       }
    }


}