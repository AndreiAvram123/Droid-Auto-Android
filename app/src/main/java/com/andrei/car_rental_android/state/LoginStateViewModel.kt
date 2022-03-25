package com.andrei.car_rental_android.state

import com.andrei.car_rental_android.baseConfig.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

abstract class LoginStateViewModel(coroutineScope: CoroutineScope?):BaseViewModel(coroutineScope) {
    abstract val authenticationState: StateFlow<SessionManager.AuthenticationState>
}

@HiltViewModel
class LoginStateViewModelImpl @Inject constructor(
    coroutineScope: CoroutineScope?,
    private val sessionManager: SessionManager
):LoginStateViewModel(coroutineScope){

    override val authenticationState: StateFlow<SessionManager.AuthenticationState>
    get() = sessionManager.authenticationState
}


