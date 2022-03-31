package com.andrei.car_rental_android.screens.Home

import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.PaymentResponse

interface ReservationStateListener {
    fun reserveCar(car:Car)
    fun cancelReservation()
    fun payUnlockFee()
    fun onUnlockPaymentIntentReady(paymentResponse: PaymentResponse)

}