package com.andrei.car_rental_android.engine.services

import com.andrei.car_rental_android.engine.configuration.ResponseWrapper
import com.andrei.car_rental_android.engine.request.LoginRequest
import com.andrei.car_rental_android.engine.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LoginService {


    @POST("/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<ResponseWrapper<LoginResponse>>
}