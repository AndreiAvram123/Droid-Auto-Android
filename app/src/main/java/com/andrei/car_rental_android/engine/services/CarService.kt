package com.andrei.car_rental_android.engine.services

import com.andrei.car_rental_android.DTOs.CarWithLocation
import com.google.android.gms.maps.model.LatLng
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

sealed interface CarService{

    @GET("/nearby")
    suspend fun getNearbyCars(
        @Query("latitude") latitude:Double,
        @Query("longitude") longitude:Double,

    ):ApiResponse<List<CarWithLocation>>


    @POST("/rides/current/car/unlock")
    suspend fun unlockCar():ApiResponse<Nothing>

    @POST("/rides/current/car/lock")
    suspend fun lockCar():ApiResponse<Nothing>

    @GET("/cars/{id}/location")
    suspend fun getCarLocation(@Path("id") carID:Long):ApiResponse<LatLng?>
}