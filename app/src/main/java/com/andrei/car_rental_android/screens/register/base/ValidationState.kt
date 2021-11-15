package com.andrei.car_rental_android.screens.register.base

sealed class ValidationState{
    object Default:ValidationState()
    object Valid:ValidationState()
    object Validating:ValidationState()
    data class ValidationError(val error:String):ValidationState()
}
