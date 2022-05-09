package com.andrei.car_rental_android.screens.verification

import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.withpersona.sdk2.inquiry.InquiryResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

abstract class DocumentVerificationViewModel(coroutineProvider:CoroutineScope?) :BaseViewModel(coroutineProvider){

    abstract fun setInquireResponse(response:InquiryResponse)
    abstract val screenState:StateFlow<ScreenState>

    sealed class  ScreenState{
        object Default:ScreenState()
        object ShowVerificationRationale:ScreenState()
        object Loading:ScreenState()
        object Error:ScreenState()
    }
}

@HiltViewModel
class DocumentVerificationViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?
):DocumentVerificationViewModel(coroutineProvider){

    override val screenState: MutableStateFlow<ScreenState> = MutableStateFlow(ScreenState.Default)

    override fun setInquireResponse(response: InquiryResponse) {
        when(response){
            is InquiryResponse.Complete -> {

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