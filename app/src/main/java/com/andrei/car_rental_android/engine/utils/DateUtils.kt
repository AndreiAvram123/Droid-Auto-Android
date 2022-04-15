package com.andrei.car_rental_android.engine.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object DateUtils {

    fun fromSecondsToDateTime(seconds:Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(seconds), ZoneId.systemDefault())
    }
}