package com.andrei.car_rental_android.engine.configuration

data class ResponseWrapper<T>(val data:T)

data class ErrorResponse(val error:String)