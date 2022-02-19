package com.andrei.car_rental_android.engine.services

import retrofit2.http.GET
import retrofit2.http.Query


interface RegisterService {
    @GET("/emailValid")
    suspend fun checkIfEmailIsUsed(@Query("email") email:String):ApiResponse<Nothing>
}