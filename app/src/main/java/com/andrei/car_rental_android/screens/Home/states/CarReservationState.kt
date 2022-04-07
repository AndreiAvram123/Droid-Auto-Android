package com.andrei.car_rental_android.screens.Home.states

import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.PaymentResponse


sealed class CarReservationState {
    object Default : CarReservationState()
    object NotAvailable:CarReservationState()
    data class TemporaryReserved(val car: Car) : CarReservationState()
    object Error : CarReservationState()
    object InProgress : CarReservationState()
    object FullyReserved:CarReservationState()

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

