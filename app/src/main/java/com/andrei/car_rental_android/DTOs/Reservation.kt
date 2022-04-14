package com.andrei.car_rental_android.DTOs

import com.google.android.gms.maps.model.LatLng

/**
 * The type of reservation that will expire if the user does not arrive
 * at the car in time
 */
 data class Reservation (
    val userID:Long,
    val car:Car,
    val carLocation:LatLng,
    val remainingTime:Int
)

