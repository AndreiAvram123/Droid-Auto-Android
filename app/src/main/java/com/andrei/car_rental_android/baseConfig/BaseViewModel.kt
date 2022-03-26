package com.andrei.car_rental_android.baseConfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

abstract class BaseViewModel(coroutineProvider: CoroutineScope? ) : ViewModel() {
    val coroutineScope = coroutineProvider ?: this.viewModelScope
}
