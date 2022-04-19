package com.andrei.car_rental_android.engine.utils

import com.andrei.car_rental_android.DTOs.*

object TestData {

    val testUser = User(
        id = 0,
        firstName = "Andrei",
        lastName = "Avram",
        email = "avramandreitiberiu@gmail.com"
    )

    val testCar = Car(
        model = CarModel(
            id = 0,
            name = "Aventator",
            manufacturerName = "Lamborghini",
            image = Image(
                url = "https://bucketeer-e0a505ee-6ef4-428f-b27b-4a4571cf883e.s3.amazonaws.com/public/lambo.jpeg"
            )
        ),
        pricePerMinute = 14,
    )

    val finishedRide = FinishedRide(
        id = 0,
        startTime = DateUtils.fromSecondsToDateTime(1649711881),
        endTime = DateUtils.fromSecondsToDateTime(1649794681),
        totalCharge = 2340,
        car = testCar
    )

    val ongoingRide = OngoingRide(
         car = testCar,
         user = testUser,
         startTime = System.currentTimeMillis()/1000L

    )
}