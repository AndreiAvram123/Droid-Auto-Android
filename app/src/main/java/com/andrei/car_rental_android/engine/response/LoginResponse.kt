package com.andrei.car_rental_android.engine.response

data class LoginResponse(
    val emailVerified:Boolean,
    val identityVerified: Boolean,
    var accessToken:String,
    var refreshToken:String
)
