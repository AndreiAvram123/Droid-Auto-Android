package com.andrei.car_rental_android.engine.configuration

data class ResponseWrapper<T>(val data:T, val isSuccessful:Boolean)

data class ErrorResponse(val error:String,val isSuccessful: Boolean)