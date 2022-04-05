package com.andrei.car_rental_android.screens.Home.states

import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.PaymentResponse


sealed class CarReservationState {
    object Default : CarReservationState()
    object NotAvailable:CarReservationState()
    data class PreReserved(val car: Car) : CarReservationState()
    object Error : CarReservationState()
    object InProgress : CarReservationState()

}

sealed class PaymentState : CarReservationState() {
    object ReadyForUnlockPayment : PaymentState()
    object LoadingPaymentData : PaymentState()
    data class PaymentDataReady(
        val paymentResponse: PaymentResponse
    ) : PaymentState()

    object PaymentFailed : PaymentState()
}

