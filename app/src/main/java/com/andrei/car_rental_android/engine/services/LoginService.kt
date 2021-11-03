package com.andrei.car_rental_android.engine.services

import com.andrei.car_rental_android.engine.configuration.ResponseWrapper
import com.andrei.car_rental_android.engine.response.LoginResponse
import retrofit2.Response
import retrofit2.http.GET

interface LoginService {

    @GET
    suspend fun login(): Response<ResponseWrapper<LoginResponse>>
}