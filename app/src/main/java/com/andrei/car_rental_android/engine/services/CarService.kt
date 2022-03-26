package com.andrei.car_rental_android.engine.services

import com.andrei.car_rental_android.DTOs.Car
import retrofit2.http.GET

sealed interface CarService{

    @GET("/nearby")
    suspend fun getNearbyCars():ApiResponse<List<Car>>
}