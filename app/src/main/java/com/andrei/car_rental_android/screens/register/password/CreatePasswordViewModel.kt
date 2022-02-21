package com.andrei.car_rental_android.screens.register.password

import com.andrei.car_rental_android.PasswordUtils.hasDigit
import com.andrei.car_rental_android.PasswordUtils.hasLowercaseLetter
import com.andrei.car_rental_android.PasswordUtils.hasMinRequiredLength
import com.andrei.car_rental_android.PasswordUtils.hasSpecialChar
import com.andrei.car_rental_android.PasswordUtils.hasUppercaseLetter
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class CreatePasswordViewModel(coroutineProvider:CoroutineScope?):BaseViewModel(coroutineProvider) {
    abstract val password:StateFlow<String>
    abstract val reenteredPassword:StateFlow<String>
    abstract val reenteredPasswordValidation:StateFlow<ReenteredPasswordValidation>
    abstract val passwordStrength:StateFlow<List<PasswordStrengthCriteria>?>
    abstract val nextButtonEnabled:StateFlow<Boolean>

    abstract fun setPassword(newPassword:String)
    abstract fun setReenteredPassword(newReenteredPassword: String)



    sealed class ReenteredPasswordValidation{
        object NotValidated:ReenteredPasswordValidation()
        object Valid:ReenteredPasswordValidation()
        object Invalid:ReenteredPasswordValidation()
    }

    enum class PasswordStrengthCriteria{
        IncludesLowercaseLetter,
        IncludesUppercaseLetter,
        IncludesNumber,
        IncludesSpecialCharacter,
        IncludesMinNumberCharacters;

    }
}

@HiltViewModel
class CreatePasswordViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?
) : CreatePasswordViewModel(coroutineProvider){

    override val password: MutableStateFlow<String> = MutableStateFlow("")
    override val reenteredPassword: MutableStateFlow<String> = MutableStateFlow("")

    override val reenteredPasswordValidation: MutableStateFlow<ReenteredPasswordValidation> = MutableStateFlow(ReenteredPasswordValidation.NotValidated)
    override val passwordStrength: MutableStateFlow<List<PasswordStrengthCriteria>?> = MutableStateFlow(null)
    override val nextButtonEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)


    override fun setPassword(newPassword: String) {
            password.tryEmit(newPassword)
    }

    override fun setReenteredPassword(newReenteredPassword: String) {
          reenteredPassword.tryEmit(newReenteredPassword)
    }

    private fun checkReenteredPassword() {
        if(reenteredPassword.value.isNotBlank()){
           if(reenteredPassword.value== password.value){
              reenteredPasswordValidation.tryEmit(ReenteredPasswordValidation.Valid)
           }else{
               reenteredPasswordValidation.tryEmit(ReenteredPasswordValidation.Invalid)
           }
        }
    }


    private suspend fun resetStates(){
        nextButtonEnabled.emit(false)
        reenteredPasswordValidation.emit(ReenteredPasswordValidation.NotValidated)
        reenteredPassword.emit("")
        passwordStrength.emit(null)
    }
    init {
        coroutineScope.launch {
            password.collectLatest {
                resetStates()
                evaluatePasswordStrength()
                checkShouldEnableNextButton()
            }
        }
        coroutineScope.launch {
            reenteredPassword.collectLatest {
                delay(1000L)
                checkReenteredPassword()
            }
        }
    }

    private fun checkShouldEnableNextButton() {
        passwordStrength.value?.let { nextButtonEnabled.tryEmit(it.containsAll(PasswordStrengthCriteria.values().toList())) }
    }


    private fun evaluatePasswordStrength() {
        val password = password.value
        if (password.isNotBlank()) {
            val passwordStrengthList = mutableListOf<PasswordStrengthCriteria>()
            PasswordStrengthCriteria.values().forEach { criteria ->
                when (criteria) {
                    PasswordStrengthCriteria.IncludesLowercaseLetter ->{
                        if (password.hasLowercaseLetter()) {
                            passwordStrengthList.add(PasswordStrengthCriteria.IncludesLowercaseLetter)
                        }
                    }
                    PasswordStrengthCriteria.IncludesUppercaseLetter -> {
                        if (password.hasUppercaseLetter()) {
                            passwordStrengthList.add(PasswordStrengthCriteria.IncludesUppercaseLetter)
                        }
                    }
                    PasswordStrengthCriteria.IncludesNumber ->{
                       if(password.hasDigit()){
                           passwordStrengthList.add(PasswordStrengthCriteria.IncludesNumber)
                       }
                    }
                    PasswordStrengthCriteria.IncludesSpecialCharacter -> {
                        if(password.hasSpecialChar()){
                            passwordStrengthList.add(PasswordStrengthCriteria.IncludesSpecialCharacter)
                        }
                    }

                    PasswordStrengthCriteria.IncludesMinNumberCharacters -> {
                       if(password.hasMinRequiredLength()){
                           passwordStrengthList.add(PasswordStrengthCriteria.IncludesMinNumberCharacters)
                       }
                    }
                }
            }

            passwordStrength.tryEmit(passwordStrengthList)
        }
    }
}