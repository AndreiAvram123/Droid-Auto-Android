package com.andrei.car_rental_android.engine.services

import com.andrei.car_rental_android.engine.response.TokenResponse
import retrofit2.http.POST
import retrofit2.http.Query

sealed interface TokenService {
    @POST("/token")
    suspend fun getNewAccessToken(@Query("refreshToken")refreshToken:String):ApiResponse<TokenResponse>
}