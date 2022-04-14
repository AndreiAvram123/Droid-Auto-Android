package com.andrei.car_rental_android.screens.receipt

import androidx.lifecycle.SavedStateHandle
import com.andrei.car_rental_android.DTOs.FinishedRide
import com.andrei.car_rental_android.baseConfig.BaseViewModel
import com.andrei.car_rental_android.engine.repositories.RideRepository
import com.andrei.car_rental_android.engine.request.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class ReceiptViewModel(coroutineProvider:CoroutineScope?) : BaseViewModel(coroutineProvider) {
  abstract fun getReceipt()
  abstract val receiptState:StateFlow<ReceiptState>

  sealed class ReceiptState{
      data class Success(val finishedRide: FinishedRide):ReceiptState()
      object Loading:ReceiptState()
      object Error:ReceiptState()
  }
}

@HiltViewModel
class ReceiptViewModelImpl @Inject constructor(
    coroutineProvider: CoroutineScope?,
    savedStateHandle: SavedStateHandle,
    private val rideRepository: RideRepository
): ReceiptViewModel(coroutineProvider) {

    private val args:ReceiptScreenNavHelper.Args  = ReceiptScreenNavHelper.parseArguments(
        savedStateHandle
    )
    override val receiptState: MutableStateFlow<ReceiptState> = MutableStateFlow(ReceiptState.Loading)

    override fun getReceipt() {
       coroutineScope.launch {
           rideRepository.getRideByID(args.rideID).collect{requestState->
               when(requestState){
                   is RequestState.Success -> {
                       receiptState.emit(ReceiptState.Success(requestState.data))
                   }
                   is RequestState.Loading -> {
                       receiptState.emit(ReceiptState.Loading)
                   }
                   else ->{
                       receiptState.emit(ReceiptState.Error)
                   }
               }
           }
       }
    }


}