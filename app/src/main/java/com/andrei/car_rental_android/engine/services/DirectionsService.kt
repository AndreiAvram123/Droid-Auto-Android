package com.andrei.car_rental_android.engine.services

import com.andrei.car_rental_android.engine.response.DirectionsResponse
import retrofit2.http.GET
import retrofit2.http.Query

sealed interface DirectionsService{

    @GET("/directions")
    suspend fun getDirections(
        @Query("startLatitude") startLatitude:Double,
        @Query("startLongitude") startLongitude:Double,
        @Query("endLatitude") endLatitude:Double,
        @Query("endLongitude") endLongitude:Double,
    ):ApiResponse<DirectionsResponse>
}