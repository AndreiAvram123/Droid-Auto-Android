package com.andrei.car_rental_android.engine.services

import com.andrei.car_rental_android.DTOs.User
import retrofit2.http.GET

sealed interface UserService{

    @GET("/users/current")
   suspend fun getCurrentUser():ApiResponse<User>
}