package com.andrei.car_rental_android.DTOs

data class OngoingRide(
    val car:Car,
    val user:Car,
    val startTime:Long
)
