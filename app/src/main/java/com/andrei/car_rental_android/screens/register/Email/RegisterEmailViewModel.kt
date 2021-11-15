package com.andrei.car_rental_android.screens.register.Email

import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.screens.register.base.ValidationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

abstract class RegisterEmailViewModel(coroutineProvider:CoroutineScope?) : BaseViewModel(coroutineProvider) {
   abstract val email:StateFlow<String>
   abstract val emailValidationState:StateFlow<ValidationState>
   abstract fun setEmail(newValue:String)
   var validationOffsetTime = 2000L
}

@HiltViewModel
class RegisterEmailViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?
):RegisterEmailViewModel(coroutineProvider){

    override val email: MutableStateFlow<String> = MutableStateFlow("")

    override val emailValidationState: MutableStateFlow<ValidationState> = MutableStateFlow(ValidationState.Default)

    override fun setEmail(newValue: String) {
       coroutineScope.launch {
           email.emit(newValue)
       }
    }
    private var validationJob: Job? = null

    init {
       coroutineScope.launch {
           email.collectLatest {
               delay(validationOffsetTime)
               validateEmail()
           }
       }
    }
    private fun validateEmail(){
        //cancel previous job if it is still running
        validationJob?.cancel("Need to cancel previous validation")

    }

}