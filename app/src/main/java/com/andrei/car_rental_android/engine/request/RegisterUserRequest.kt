package com.andrei.car_rental_android.engine.request

data class RegisterUserRequest(
    val firstName:String = "",
    val lastName:String = "",
    val email :String ="",
    val password:String = "",
)