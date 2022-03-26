package com.andrei.car_rental_android.screens.Home

import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.repositories.CarRepository
import com.andrei.car_rental_android.engine.request.RequestState
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class HomeViewModel(coroutineProvider:CoroutineScope?): BaseViewModel(coroutineProvider) {
    abstract val nearbyCars:StateFlow<HomeViewModelState>
    abstract fun getNearbyCars()

    sealed class HomeViewModelState{
        data class Success(val data:List<Car>):HomeViewModelState()
        object Loading:HomeViewModelState()
        object Error:HomeViewModelState()
    }
}

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?,
    private val carRepository: CarRepository
):HomeViewModel(coroutineProvider){

    override val nearbyCars: MutableStateFlow<HomeViewModelState> = MutableStateFlow(HomeViewModelState.Loading)

    override fun getNearbyCars() {
        coroutineScope.launch {
            carRepository.fetchNearby(LatLng(0.0, 0.0)).collect {requestState->
               when(requestState){
                   is RequestState.Success->{
                       nearbyCars.emit(HomeViewModelState.Success(requestState.data))
                   }
               }
            }
        }
    }


}
