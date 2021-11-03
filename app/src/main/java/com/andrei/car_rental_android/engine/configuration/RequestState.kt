package com.andrei.car_rental_android.engine.configuration

sealed class RequestState<out T> {
    data class Success<T>(val data:T):RequestState<T>()
    object Loading:RequestState<Nothing>()
    data class Error(val error:String,val code:Int):RequestState<Nothing>()
}