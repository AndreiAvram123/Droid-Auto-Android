package com.andrei.car_rental_android.DTOs

data class FinishedRide(
    val id:Long,
    //time in unix seconds
    val startTime:Long,
    val endTime:Long,
    val totalCharge:Long,
    val car: Car
)