package com.andrei.car_rental_android.DTOs

 data class TemporaryReservation (
     val userID:Long,
     val car:Car,
     val remainingTime:Int
)
