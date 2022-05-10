package com.andrei.car_rental_android.screens.verification

import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.state.LocalRepository
import com.andrei.car_rental_android.state.SessionManager
import com.andrei.car_rental_android.state.SessionUserState
import com.withpersona.sdk2.inquiry.InquiryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class DocumentVerificationViewModel(coroutineProvider:CoroutineScope?) :BaseViewModel(coroutineProvider){

    abstract fun setInquireResponse(response:InquiryResponse)
    abstract val screenState:StateFlow<ScreenState>
    abstract val sessionUserState:StateFlow<SessionUserState>
    sealed class  ScreenState{
        object Default:ScreenState()
        object ShowVerificationRationale:ScreenState()
        object Loading:ScreenState()
        object Error:ScreenState()
    }
}

@HiltViewModel
class DocumentVerificationViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?,
    private val sessionManager: SessionManager,
    private val localRepository: LocalRepository
):DocumentVerificationViewModel(coroutineProvider){

    override val screenState: MutableStateFlow<ScreenState> = MutableStateFlow(ScreenState.Default)
    override val sessionUserState: StateFlow<SessionUserState> = sessionManager.sessionUserState



    override fun setInquireResponse(response: InquiryResponse) {
        when(response){
            is InquiryResponse.Complete -> {
               coroutineScope.launch {
                   localRepository.setIdentityVerified(true)
               }
            }
            is InquiryResponse.Cancel ->{
                screenState.tryEmit(ScreenState.ShowVerificationRationale)
            }
            is InquiryResponse.Error -> {
                screenState.tryEmit(ScreenState.Error)
            }
        }
    }
}