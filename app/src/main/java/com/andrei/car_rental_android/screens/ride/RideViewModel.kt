package com.andrei.car_rental_android.screens.ride

import com.andrei.car_rental_android.baseConfig.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

abstract class RideViewModel(coroutineProvider: CoroutineScope?): BaseViewModel(coroutineProvider) {
}

@HiltViewModel
class RideViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?,
): RideViewModel(coroutineProvider){

}