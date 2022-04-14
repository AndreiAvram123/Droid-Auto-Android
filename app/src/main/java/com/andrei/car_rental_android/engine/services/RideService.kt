package com.andrei.car_rental_android.engine.services

import com.andrei.car_rental_android.DTOs.FinishedRide
import com.andrei.car_rental_android.DTOs.OngoingRide
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

sealed interface RideService{

    @GET("/rides/ongoing")
    suspend fun getOngoingRide():ApiResponse<OngoingRide?>

     @DELETE("/rides/ongoing")
     suspend fun finishOngoingRide():ApiResponse<FinishedRide>

    @GET("/rides/{id}")
    suspend fun getRideByID(@Path("id") id:Long):ApiResponse<FinishedRide>

}