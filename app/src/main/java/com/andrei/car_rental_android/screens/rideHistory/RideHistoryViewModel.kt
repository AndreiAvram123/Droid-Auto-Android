package com.andrei.car_rental_android.screens.rideHistory

import com.andrei.car_rental_android.DTOs.FinishedRide
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.repositories.RideRepository
import com.andrei.car_rental_android.engine.request.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

abstract class RideHistoryViewModel(coroutineProvider : CoroutineScope?) : BaseViewModel(coroutineProvider) {

    abstract val screenState:StateFlow<ScreenState>

    sealed class ScreenState{
        data class Success(val data:List<FinishedRide>): ScreenState()
        object Loading:ScreenState()
        object Error:ScreenState()
    }
}

@HiltViewModel
class RideHistoryViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?,
    private val rideRepository: RideRepository
):RideHistoryViewModel(coroutineProvider){

    override val screenState: StateFlow<ScreenState> = rideRepository.getRideHistory().transform {requestState->
        when(requestState){
            is RequestState.Success -> emit(ScreenState.Success(
                requestState.data
            ))
            is RequestState.Loading -> emit(ScreenState.Loading)
            else -> emit(ScreenState.Error)

        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScreenState.Loading
    )


}