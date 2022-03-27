package com.andrei.car_rental_android.engine.repositories

import com.andrei.car_rental_android.engine.configuration.RequestExecutor
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.response.TokenResponse
import com.andrei.car_rental_android.engine.services.NewTokenRequest
import com.andrei.car_rental_android.engine.services.TokenService
import com.andrei.car_rental_android.state.LocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface TokenRepository {
    fun getNewAccessToken():Flow<RequestState<TokenResponse>>
}


class TokenRepositoryImpl @Inject constructor(
    private val requestExecutor: RequestExecutor,
    private val localRepository:LocalRepository,
    private val tokenService:TokenService
):TokenRepository {

    override fun getNewAccessToken(): Flow<RequestState<TokenResponse>>  = flow {
        val refreshToken = localRepository.refreshTokenFlow.firstOrNull()
        if (refreshToken != null){
            requestExecutor.performRequest {
                tokenService.getNewAccessToken(NewTokenRequest(
                    refreshToken = refreshToken
                ))
            }.collect{
                emit(it)
            }
        }else{
            emit(RequestState.Error("No refresh token present"))
        }
    }
}