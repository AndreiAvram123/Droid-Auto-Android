package com.andrei.car_rental_android.engine.configuration

sealed class RequestState<out T> {
    data class Success<T>(val data:T):RequestState<T>()
    object Loading:RequestState<Nothing>()
    object ConnectionError:RequestState<Nothing>()
    data class Error(val message:String, val code:Int? = null):RequestState<Nothing>()
}