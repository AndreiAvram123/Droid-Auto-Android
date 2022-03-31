package com.andrei.car_rental_android.engine.services

import com.andrei.car_rental_android.DTOs.PaymentResponse
import retrofit2.http.POST

sealed interface PaymentService{
    @POST("/payment/unlock_fee")
    suspend fun makeUnlockFeePayment():ApiResponse<PaymentResponse>
}