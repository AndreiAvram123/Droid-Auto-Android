package com.andrei.car_rental_android.engine.utils

import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.CarModel
import com.andrei.car_rental_android.DTOs.FinishedRide
import com.andrei.car_rental_android.DTOs.Image

object TestData {
    val finishedRide = FinishedRide(
        id = 0,
        startTime = DateUtils.fromSecondsToDateTime(1649711881),
        endTime = DateUtils.fromSecondsToDateTime(1649794681),
        totalCharge = 2340,
        car = Car(
            model = CarModel(
                id = 0,
                name = "Aventator",
                manufacturerName = "Lamborghini",
                image = Image(
                    url = "https://bucketeer-e0a505ee-6ef4-428f-b27b-4a4571cf883e.s3.amazonaws.com/public/lambo.jpeg"
                )
            ),
            pricePerMinute = 14.3,
        )
    )
}