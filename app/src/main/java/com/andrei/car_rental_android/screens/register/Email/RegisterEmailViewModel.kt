package com.andrei.car_rental_android.screens.register.Email

import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.repositories.RegisterRepository
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.ui.utils.emailRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

abstract class RegisterEmailViewModel(coroutineProvider:CoroutineScope?) : BaseViewModel(coroutineProvider) {
    abstract val email:StateFlow<String>
    abstract val emailValidationState:StateFlow<EmailValidationState>
    abstract val nextButtonEnabled:StateFlow<Boolean>
    abstract fun setEmail(newValue:String)
    var validationOffsetTime = 1000L

    sealed class EmailValidationState{
        object Default: EmailValidationState()
        object Validating:EmailValidationState()
        object Valid:EmailValidationState()

        sealed class EmailValidationError:EmailValidationState(){
            object EmailAlreadyTaken: EmailValidationError()
            object Unknown:EmailValidationError()
            object InvalidFormat: EmailValidationError()
        }
    }
}

@HiltViewModel
class RegisterEmailViewModelImpl @Inject  constructor(
    coroutineProvider: CoroutineScope?,
    private val registerRepository: RegisterRepository
):RegisterEmailViewModel(coroutineProvider){

    override val email: MutableStateFlow<String> = MutableStateFlow("")

    override val emailValidationState: MutableStateFlow<EmailValidationState> = MutableStateFlow(EmailValidationState.Default)
    override val nextButtonEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override fun setEmail(newValue: String) {
          email.tryEmit(newValue)
    }
    private var validationJob: Job? = null

    init {
        coroutineScope.launch {
            email.collect {
                emailValidationState.emit(EmailValidationState.Default)
                cancelPreviousValidation()
                if(it.isNotBlank()){
                    delay(validationOffsetTime)
                    validateEmail()
                }
            }
        }
        coroutineScope.launch {
            emailValidationState.collect {
                nextButtonEnabled.emit(it is EmailValidationState.Valid)
            }
        }
    }


    private fun cancelPreviousValidation(){
        //cancel previous job if it is still running
        validationJob?.cancel("Need to cancel previous validation")
    }
    private fun validateEmail(){

        if(email.value.isNotBlank()) {
            if(email.value.isEmailValid()) {
                validationJob = coroutineScope.launch {
                    registerRepository.checkIfEmailIsUsed(email.value).collect { requestState ->
                        when (requestState) {
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
            }else{
                emailValidationState.tryEmit(EmailValidationState.EmailValidationError.InvalidFormat)
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
    private fun String.isEmailValid():Boolean = this.matches(emailRegex)

}