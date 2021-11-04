package com.andrei.car_rental_android.engine.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val username:String,
    val password:String
)
