package com.andrei.car_rental_android.engine.services

import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.engine.response.ReservationRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

sealed interface CarService{

    @GET("/nearby")
    suspend fun getNearbyCars():ApiResponse<List<Car>>

    @POST("/reservation")
    suspend fun makeReservation(@Body reservationRequest: ReservationRequest):ApiResponse<Nothing>

}