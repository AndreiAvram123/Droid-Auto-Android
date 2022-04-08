package com.andrei.car_rental_android.engine.services

import com.andrei.car_rental_android.DTOs.Car
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

sealed interface CarService{

    @GET("/nearby")
    suspend fun getNearbyCars(
        @Query("latitude") latitude:Double,
        @Query("longitude") longitude:Double,

    ):ApiResponse<List<Car>>


    @POST("/reservation/car/unlock")
    suspend fun unlockCar():ApiResponse<Nothing>
}