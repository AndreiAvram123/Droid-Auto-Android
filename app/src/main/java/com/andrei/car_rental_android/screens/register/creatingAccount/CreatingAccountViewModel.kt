package com.andrei.car_rental_android.screens.register.creatingAccount

import androidx.lifecycle.SavedStateHandle
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.configuration.RequestState
import com.andrei.car_rental_android.engine.repositories.RegisterRepository
import com.andrei.car_rental_android.engine.request.RegisterUserRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class CreatingAccountViewModel(coroutineProvider:CoroutineScope?): BaseViewModel(coroutineProvider) {
    abstract val creatingAccountState:StateFlow<CreatingAccountState>
    abstract fun retry()

    sealed class CreatingAccountState{
        object Created:CreatingAccountState()
        object Loading: CreatingAccountState()
        object Error:CreatingAccountState()
    }
}

@HiltViewModel
class CreatingAccountViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?,
    savedStateHandle: SavedStateHandle,
    private val registerRepository: RegisterRepository
): CreatingAccountViewModel(coroutineProvider){

    private val args:CreatingAccountNavHelper.Args = CreatingAccountNavHelper.parseArguments(
        savedStateHandle
    )

    override val creatingAccountState: MutableStateFlow<CreatingAccountState> = MutableStateFlow(CreatingAccountState.Loading)

    override fun retry() {
        createAccount()
    }

    init {
      createAccount()
    }

    private fun createAccount(){
        coroutineScope.launch {
           registerRepository.registerUser(args.toRegisterUserRequest()).collect { requestState->
               when(requestState){
                   is RequestState.Success -> {
                       creatingAccountState.emit(CreatingAccountState.Created)
                   }
                   is RequestState.Loading->{
                       creatingAccountState.emit(CreatingAccountState.Loading)
                   }
                   else ->{
                       creatingAccountState.emit(CreatingAccountState.Error)
                   }
               }
           }
        }
    }

    private fun CreatingAccountNavHelper.Args.toRegisterUserRequest():RegisterUserRequest = RegisterUserRequest(
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        password = this.password

    )

}