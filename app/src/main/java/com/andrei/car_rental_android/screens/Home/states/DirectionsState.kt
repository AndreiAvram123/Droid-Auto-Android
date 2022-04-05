package com.andrei.car_rental_android.screens.Home.states

import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.response.DirectionStep
import com.andrei.car_rental_android.engine.response.DirectionsResponse

sealed class DirectionsState {
    data class Success(val directions: List<DirectionStep>) : DirectionsState()
    object Loading : DirectionsState()
    object Error : DirectionsState()
    object Default : DirectionsState()

    companion object{
        fun RequestState<DirectionsResponse>.toState():DirectionsState{
            return when(this){
                is RequestState.Success ->Success(data.steps)
                is RequestState.Loading -> Loading
                else -> Error
            }
        }

    }
}