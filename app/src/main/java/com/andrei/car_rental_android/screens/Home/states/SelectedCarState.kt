package com.andrei.car_rental_android.screens.Home.states

import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.PaymentResponse
import kotlin.time.Duration


sealed class SelectedCarState {
    object Default : SelectedCarState()
    object NotAvailable:SelectedCarState()
    data class Reserved(
        val car: Car,
        val remainingTime:Duration
        ) : SelectedCarState()
    object Error : SelectedCarState()
    object InProgress : SelectedCarState()
    object UnlockingCar:SelectedCarState()

}

sealed class UnlockPaymentState {
    object Default:UnlockPaymentState()
    object ReadyForUnlockUnlockPayment : UnlockPaymentState()
    object LoadingUnlockPaymentData : UnlockPaymentState()
    data class UnlockPaymentDataReady(
        val paymentResponse: PaymentResponse
    ) : UnlockPaymentState()

    object UnlockPaymentFailed : UnlockPaymentState()
}

