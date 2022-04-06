package com.andrei.car_rental_android.screens.Home.states

import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.engine.request.RequestState

sealed class HomeViewModelState {
    data class Success(val data: List<Car>) : HomeViewModelState()
    object Loading : HomeViewModelState()
    object Error : HomeViewModelState()
    companion object {
        fun RequestState<List<Car>>.toHomeViewModelState() =
            when (this) {
                is RequestState.Success -> Success(this.data)
                is RequestState.Loading -> Loading
                else -> Error
            }
    }
}