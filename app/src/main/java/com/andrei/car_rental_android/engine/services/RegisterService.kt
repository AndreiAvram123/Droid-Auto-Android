package com.andrei.car_rental_android.engine.services

import com.andrei.car_rental_android.engine.request.RegisterUserRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


sealed interface RegisterService {
    @GET("/email/valid")
    suspend fun checkIfEmailIsUsed(@Query("email") email:String):ApiResponse<Nothing>

    @POST("/register")
    suspend fun registerUser(@Body registerUserRequest: RegisterUserRequest):ApiResponse<Nothing>
}