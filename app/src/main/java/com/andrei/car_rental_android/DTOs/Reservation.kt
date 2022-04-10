package com.andrei.car_rental_android.DTOs

/**
 * The type of reservation that will expire if the user does not arrive
 * at the car in time
 */
 data class Reservation (
     val userID:Long,
     val car:Car,
     val remainingTime:Int
)

