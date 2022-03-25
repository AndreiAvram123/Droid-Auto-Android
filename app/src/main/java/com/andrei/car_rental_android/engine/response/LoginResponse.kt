package com.andrei.car_rental_android.engine.response

data class LoginResponse(
    val isEmailVerified:Boolean,
    val accessToken:String,
    val refreshToken:String
)
