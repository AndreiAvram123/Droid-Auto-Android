package com.andrei.car_rental_android.engine.services

import com.andrei.car_rental_android.engine.response.ReservationRequest
import com.andrei.car_rental_android.engine.response.Reservation
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

sealed interface ReservationService{
    @POST("/reservation")
    suspend fun makeReservation(@Body reservationRequest: ReservationRequest):ApiResponse<Nothing>

    @DELETE("/reservation")
    suspend fun cancelReservation():ApiResponse<Nothing>

    @GET("/reservations/current")
    suspend fun getCurrentReservation():ApiResponse<Reservation?>
}