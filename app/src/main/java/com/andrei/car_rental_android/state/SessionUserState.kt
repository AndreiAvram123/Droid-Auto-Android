package com.andrei.car_rental_android.state

import com.andrei.car_rental_android.DTOs.User

sealed class SessionUserState{
    object Default:SessionUserState()
    data class Loaded(val user:User):SessionUserState()
    object LoadingUser:SessionUserState()
    object ErrorLoadingUser:SessionUserState()
}