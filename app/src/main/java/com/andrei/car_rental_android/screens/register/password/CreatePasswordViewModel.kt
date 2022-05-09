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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class CreatePasswordViewModel(coroutineProvider:CoroutineScope?):BaseViewModel(coroutineProvider) {
    abstract val password:StateFlow<String>
    abstract val reenteredPassword:StateFlow<String>
    abstract val reenteredPasswordValidation:StateFlow<ReenteredPasswordValidation>
    abstract val passwordStrength:StateFlow<List<PasswordRequirement>>
    abstract val nextButtonEnabled:StateFlow<Boolean>

    abstract fun setPassword(newPassword:String)
    abstract fun setReenteredPassword(newReenteredPassword: String)


    enum class PasswordRequirement{
        IncludesLowercaseLetter,
        IncludesUppercaseLetter,
        IncludesDigit,
        IncludesSpecialCharacter,
        IncludesMinNumberCharacters;

    }

    sealed class ReenteredPasswordValidation{
        object NotValidated:ReenteredPasswordValidation()
        object Valid:ReenteredPasswordValidation()
        object Invalid:ReenteredPasswordValidation()
    }


}

@HiltViewModel
class CreatePasswordViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?
) : CreatePasswordViewModel(coroutineProvider){

    override val password: MutableStateFlow<String> = MutableStateFlow("")
    override val reenteredPassword: MutableStateFlow<String> = MutableStateFlow("")

    override val reenteredPasswordValidation: MutableStateFlow<ReenteredPasswordValidation> = MutableStateFlow(ReenteredPasswordValidation.NotValidated)
    override val passwordStrength: MutableStateFlow<List<PasswordRequirement>> = MutableStateFlow(emptyList())
    override val nextButtonEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)


    override fun setPassword(newPassword: String) {
            password.tryEmit(newPassword)
    }

    override fun setReenteredPassword(newReenteredPassword: String) {
          reenteredPassword.tryEmit(newReenteredPassword)
    }

    private fun checkReenteredPassword() {
        if(reenteredPassword.value == password.value){
            reenteredPasswordValidation.tryEmit(ReenteredPasswordValidation.Valid)
        }else{
            reenteredPasswordValidation.tryEmit(ReenteredPasswordValidation.Invalid)
        }
    }


    private suspend fun resetStates(){
        reenteredPasswordValidation.emit(ReenteredPasswordValidation.NotValidated)
        reenteredPassword.emit("")
    }
    init {
        coroutineScope.launch {
            password.collectLatest {
                resetStates()
                evaluatePasswordStrength()
            }
        }
        coroutineScope.launch {
            reenteredPassword.collectLatest {
                delay(500L)
                checkReenteredPassword()
            }
        }
        coroutineScope.launch {
            combine(reenteredPasswordValidation, passwordStrength) { passwordValidation, strength ->
                passwordValidation is ReenteredPasswordValidation.Valid && strength.containsAll(
                    PasswordRequirement.values().toList()
                )
            }.collect {
                nextButtonEnabled.emit(it)
            }
        }
    }


    private fun evaluatePasswordStrength() {
        val password = password.value
         if(password.isBlank()){
             passwordStrength.tryEmit(emptyList())
         }

        if (password.isNotBlank()) {
            val passwordStrengthList = mutableListOf<PasswordRequirement>()
            PasswordRequirement.values().forEach { criteria ->
                when (criteria) {
                    PasswordRequirement.IncludesLowercaseLetter ->{
                        if (password.hasLowercaseLetter()) {
                            passwordStrengthList.add(PasswordRequirement.IncludesLowercaseLetter)
                        }
                    }
                    PasswordRequirement.IncludesUppercaseLetter -> {
                        if (password.hasUppercaseLetter()) {
                            passwordStrengthList.add(PasswordRequirement.IncludesUppercaseLetter)
                        }
                    }
                    PasswordRequirement.IncludesDigit ->{
                       if(password.hasDigit()){
                           passwordStrengthList.add(PasswordRequirement.IncludesDigit)
                       }
                    }
                    PasswordRequirement.IncludesSpecialCharacter -> {
                        if(password.hasSpecialChar()){
                            passwordStrengthList.add(PasswordRequirement.IncludesSpecialCharacter)
                        }
                    }

                    PasswordRequirement.IncludesMinNumberCharacters -> {
                       if(password.hasMinRequiredLength()){
                           passwordStrengthList.add(PasswordRequirement.IncludesMinNumberCharacters)
                       }
                    }
                }
            }

            passwordStrength.tryEmit(passwordStrengthList)
        }
    }
}