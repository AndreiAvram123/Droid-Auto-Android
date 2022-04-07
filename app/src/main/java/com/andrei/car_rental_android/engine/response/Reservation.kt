package com.andrei.car_rental_android.engine.response

import com.andrei.car_rental_android.DTOs.TemporaryReservation

data class Reservation(
    val isTemporary:Boolean,
    val temporaryReservation: TemporaryReservation?
)
