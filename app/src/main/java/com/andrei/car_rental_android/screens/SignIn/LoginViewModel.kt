package com.andrei.car_rental_android.screens.SignIn

import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.repositories.LoginRepository
import com.andrei.car_rental_android.engine.request.LoginRequest
import com.andrei.car_rental_android.engine.request.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class LoginViewModel(coroutineProvider: CoroutineScope?) : BaseViewModel(coroutineProvider){
    abstract val emailState:StateFlow<String>
    abstract val passwordState:StateFlow<String>
    abstract val loginUiState:StateFlow<LoginUIState>
    abstract fun resetUIState()
    abstract fun setEmail(username:String)
    abstract fun setPassword(password:String)
    abstract fun login()

    sealed class LoginUIState {
        object Default:LoginUIState()
        object LoggedIn:LoginUIState()
        object Loading:LoginUIState()
        object ServerError:LoginUIState()
        object InvalidCredentials:LoginUIState()

    }
}

@HiltViewModel
class LoginViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?,
    private val loginRepository: LoginRepository
): LoginViewModel(coroutineProvider){

    override val emailState: MutableStateFlow<String> = MutableStateFlow("")
    override val passwordState: MutableStateFlow<String> = MutableStateFlow("")
    override val loginUiState: MutableStateFlow<LoginUIState> = MutableStateFlow(LoginUIState.Default)

    override fun resetUIState() {
        coroutineScope.launch {
            loginUiState.emit(LoginUIState.Default)
        }
    }

    override fun setEmail(username: String) {
        coroutineScope.launch {
            emailState.emit(username)
        }
    }
    override fun setPassword(password: String) {
        coroutineScope.launch {
            passwordState.emit(password)
        }
    }

    override fun login() {
         coroutineScope.launch {
             val username = emailState.value.trim()
             val password = passwordState.value.trim()

             if (username.isNotBlank() && password.isNotBlank()) {
                 val loginRequest = LoginRequest(
                     email = username,
                     password = password
                 )
                 loginRepository.login(loginRequest).collect { response ->
                     when (response) {
                         is RequestState.Success -> {
                             loginUiState.emit(LoginUIState.LoggedIn)
                         }
                         is RequestState.Loading -> {
                             loginUiState.emit(LoginUIState.Loading)
                         }
                         is RequestState.Error -> {
                             if (response.code == 401) {
                                 loginUiState.emit(LoginUIState.InvalidCredentials)
                             } else {
                                 loginUiState.emit(LoginUIState.ServerError)
                             }
                         }
                         is RequestState.ConnectionError -> {
                             loginUiState.emit(LoginUIState.ServerError)
                         }
                     }
                 }
             }
         }
    }

}