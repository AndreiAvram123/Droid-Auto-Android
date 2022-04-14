package com.andrei.car_rental_android.engine.services

import com.andrei.car_rental_android.DTOs.Reservation
import com.andrei.car_rental_android.engine.response.ReservationRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

sealed interface ReservationService{
    @POST("/reservations")
    suspend fun makeReservation(@Body reservationRequest: ReservationRequest):ApiResponse<Reservation>

    @DELETE("/reservations/current")
    suspend fun cancelReservation():ApiResponse<Nothing>

    @GET("/reservations/current")
    suspend fun getCurrentReservation():ApiResponse<Reservation?>
}