package com.andrei.car_rental_android.DTOs

data class PaymentResponse(
    val clientSecret:String,
    val publishableKey:String,
    val customerID:String,
    val customerKey:String
)
