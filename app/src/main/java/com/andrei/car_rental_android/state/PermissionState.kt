package com.andrei.car_rental_android.state

sealed class PermissionState{
    object Unchecked:PermissionState()
    object Denied:PermissionState()
    object Granted:PermissionState()
}