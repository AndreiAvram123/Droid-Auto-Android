package com.andrei.car_rental_android.engine.services

import com.andrei.car_rental_android.engine.response.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

sealed interface TokenService {
    @POST("/token")
    suspend fun getNewAccessToken(@Body newTokenRequest: NewTokenRequest):ApiResponse<TokenResponse>
}

data class NewTokenRequest(
    val refreshToken: String
)