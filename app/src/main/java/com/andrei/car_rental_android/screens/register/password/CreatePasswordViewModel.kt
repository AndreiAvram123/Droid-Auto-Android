package com.andrei.car_rental_android.screens.register.password

import com.andrei.car_rental_android.PasswordUtils.hasLowercaseLetter
import com.andrei.car_rental_android.PasswordUtils.hasUppercaseLetter
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class CreatePasswordViewModel(coroutineProvider:CoroutineScope?):BaseViewModel(coroutineProvider) {
    abstract val passwordState:StateFlow<String>
    abstract val passwordStrength:StateFlow<List<PasswordStrengthCriteria>?>
    abstract val nextButtonEnabled:StateFlow<Boolean>

    abstract fun setPassword(newPassword:String)



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

    override val passwordState: MutableStateFlow<String> = MutableStateFlow("")
    override val passwordStrength: MutableStateFlow<List<PasswordStrengthCriteria>?> = MutableStateFlow(null)
    override val nextButtonEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)


    override fun setPassword(newPassword: String) {
       coroutineScope.launch {
           passwordState.emit(newPassword)
       }
    }
    private suspend fun resetStates(){
            nextButtonEnabled.emit(false)
            passwordStrength.emit(null)
    }
    init {
        coroutineScope.launch {
            passwordState.collectLatest {
                resetStates()
                evaluatePasswordStrength()
                checkShouldEnableNextButton()
            }
        }
    }

    private fun checkShouldEnableNextButton() {
        passwordStrength.value?.let { nextButtonEnabled.tryEmit(it.containsAll(PasswordStrengthCriteria.values().toList())) }
    }


    private fun evaluatePasswordStrength() {
        val password = passwordState.value
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
                    PasswordStrengthCriteria.IncludesNumber -> R.string.screen_password_include_number
                    PasswordStrengthCriteria.IncludesSpecialCharacter -> R.string.screen_password_include_special_character
                    PasswordStrengthCriteria.IncludesMinNumberCharacters -> R.string.screen_password_include_8_characters
                }
            }

            passwordStrength.tryEmit(passwordStrengthList)
        }
    }
}