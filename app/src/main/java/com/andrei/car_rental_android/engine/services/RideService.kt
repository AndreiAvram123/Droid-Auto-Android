package com.andrei.car_rental_android.engine.services

import com.andrei.car_rental_android.DTOs.OngoingRide
import retrofit2.http.GET

sealed interface RideService{

    @GET("/rides/ongoing")
    suspend fun getOngoingRide():ApiResponse<OngoingRide?>


}