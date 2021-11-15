package com.andrei.car_rental_android.screens.register.Email

import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.configuration.RequestState
import com.andrei.car_rental_android.engine.repositories.RegisterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

abstract class RegisterEmailViewModel(coroutineProvider:CoroutineScope?) : BaseViewModel(coroutineProvider) {
   abstract val email:StateFlow<String>
   abstract val emailValidationState:StateFlow<EmailValidationState>
   abstract fun setEmail(newValue:String)
   var validationOffsetTime = 2000L

    sealed class EmailValidationState{
        object Default: EmailValidationState()
        object Validating:EmailValidationState()
        object Valid:EmailValidationState()

        sealed class EmailValidationError:EmailValidationState(){
            object EmailAlreadyTaken: EmailValidationError()
            object Unknown:EmailValidationError()
        }
    }
}

@HiltViewModel
class RegisterEmailViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?,
    private val registerRepository: RegisterRepository
):RegisterEmailViewModel(coroutineProvider){

    override val email: MutableStateFlow<String> = MutableStateFlow("")

    override val emailValidationState: MutableStateFlow<EmailValidationState> = MutableStateFlow(EmailValidationState.Default)

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
        if(email.value.isEmailValid()) {
            validationJob = coroutineScope.launch {
                registerRepository.checkIfEmailIsUsed(email.value).collect { requestState -> when (requestState) {
                        is RequestState.Success -> {
                            emailValidationState.emit(EmailValidationState.Valid)
                        }
                        is RequestState.ConnectionError -> {

                        }
                        is RequestState.Error -> {
                            emailValidationState.emit(requestState.mapToEmailValidationError())
                        }
                        is RequestState.Loading -> {
                            emailValidationState.emit(EmailValidationState.Validating)
                        }
                    }
                }
            }
        }
    }

    private fun RequestState.Error.mapToEmailValidationError(): EmailValidationState.EmailValidationError {
        return when(this.message){
            RegisterRepository.errorEmailUsed->{
                EmailValidationState.EmailValidationError.EmailAlreadyTaken
            }
            else ->{
                EmailValidationState.EmailValidationError.Unknown
            }
        }
    }
    private fun String.isEmailValid():Boolean {
        return this.isNotBlank()
    }
}