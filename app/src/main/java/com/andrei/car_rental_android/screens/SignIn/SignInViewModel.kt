package com.andrei.car_rental_android.screens.SignIn

import android.content.Context
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class SignInViewModel(coroutineProvider: CoroutineScope?) : BaseViewModel(coroutineProvider){
    abstract val usernameState:StateFlow<String>
    abstract val passwordState:StateFlow<String>
    abstract fun setUsername(username:String)
    abstract fun setPassword(password:String)
    abstract fun login()
}

@HiltViewModel
class SignInViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?
): SignInViewModel(coroutineProvider){

    override val usernameState: MutableStateFlow<String> = MutableStateFlow("")
    override val passwordState: MutableStateFlow<String> = MutableStateFlow("")

    override fun setUsername(username: String) {
        coroutineScope.launch {
            usernameState.emit(username)
        }
    }
    override fun setPassword(password: String) {
        coroutineScope.launch {
            passwordState.emit(password)
        }
    }

    override fun login() {

    }

}