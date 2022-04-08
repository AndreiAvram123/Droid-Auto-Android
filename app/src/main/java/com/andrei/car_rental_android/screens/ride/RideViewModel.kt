package com.andrei.car_rental_android.screens.ride

import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.repositories.RideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class RideViewModel(coroutineProvider: CoroutineScope?): BaseViewModel(coroutineProvider) {

    abstract fun finishRide()
    protected abstract fun getOngoingRide()
}

@HiltViewModel
class RideViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?,
    private val rideRepository: RideRepository
): RideViewModel(coroutineProvider){

    init {
        //getting to this screen implies that an ongoing ride
    }

    override fun finishRide() {
        coroutineScope.launch {

        }
    }

    override fun getOngoingRide() {
        coroutineScope.launch {

        }
    }
}