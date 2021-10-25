package com.andrei.car_rental_android

sealed class RequestState<out T> {
    data class Success<T> (val data: T): RequestState<T>()
    object Loading:RequestState<Nothing>()
}