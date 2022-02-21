package com.andrei.car_rental_android.screens.register

import com.andrei.car_rental_android.baseConfig.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class UsernameViewModel(coroutineProvider:CoroutineScope?) : BaseViewModel(coroutineProvider) {
    abstract val firstName:StateFlow<String>
    abstract val surname:StateFlow<String>
    abstract fun setFirstName(newName:String)
    abstract fun setSurname(newName:String)
    abstract val nextButtonEnabled:StateFlow<Boolean>
}

@HiltViewModel
class UsernameViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?
):UsernameViewModel(coroutineProvider){

    override val firstName: MutableStateFlow<String> = MutableStateFlow("")

    override val surname: MutableStateFlow<String> = MutableStateFlow("")

    override fun setFirstName(newName: String) {
        coroutineScope.launch {
            firstName.emit(newName)
        }
    }

    override fun setSurname(newName: String) {
        coroutineScope.launch {
            surname.emit(newName)
        }
    }


    override val nextButtonEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        coroutineScope.launch {
            combine(firstName, surname){
                firstNameValue,surnameValue -> firstNameValue.isNotBlank() && surnameValue.isNotBlank()
            }.collect {
                nextButtonEnabled.emit(it)
            }
        }
    }

}