package com.andrei.car_rental_android.DTOs

import java.time.LocalDateTime

data class FinishedRide(
    val id:Long,
    //time in unix seconds
    val startTime:LocalDateTime,
    val endTime:LocalDateTime,
    val totalCharge:Long,
    val car: Car
)
