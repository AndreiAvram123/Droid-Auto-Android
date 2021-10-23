package com.andrei.car_rental_android.screens.SignIn

import android.content.Context
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

abstract class SignInViewModel(coroutineProvider: CoroutineScope?) : BaseViewModel(coroutineProvider){
}

@HiltViewModel
class SignInViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?
): SignInViewModel(coroutineProvider){

}