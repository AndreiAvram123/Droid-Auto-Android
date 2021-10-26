package com.andrei.car_rental_android.screens.Home

import com.andrei.car_rental_android.RequestState
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.CarRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class HomeViewModel(coroutineProvider:CoroutineScope?): BaseViewModel(coroutineProvider) {
    abstract val nearbyCars:StateFlow<RequestState<List<LatLng>>>
}

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?,
    private val carRepository: CarRepository
):HomeViewModel(coroutineProvider){

    override val nearbyCars: MutableStateFlow<RequestState<List<LatLng>>> = MutableStateFlow(RequestState.Loading)

}
