package com.andrei.car_rental_android.screens.Home

import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.google.android.libraries.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class HomeViewModel(coroutineProvider:CoroutineScope?): BaseViewModel(coroutineProvider) {
    abstract val nearbyCars:StateFlow<List<LatLng>>
}

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?
):HomeViewModel(coroutineProvider){

    override val nearbyCars: MutableStateFlow<List<LatLng>> = MutableStateFlow(listOf())

    init {
        coroutineScope.launch {
            delay(3000)
            nearbyCars.emit(listOf(LatLng(44.4268,26.1025)))
        }
    }
}