package com.andrei.car_rental_android.DTOs

data class FinishedRide(
    val id:Long,
    //time in unix seconds
    val startedTime:Long,
    val totalCharge:Double,
    val car: Car
)