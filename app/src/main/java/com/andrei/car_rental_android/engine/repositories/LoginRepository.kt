package com.andrei.car_rental_android.engine.repositories

import com.andrei.car_rental_android.engine.configuration.RequestExecutor
import com.andrei.car_rental_android.engine.request.LoginRequest
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.response.LoginResponse
import com.andrei.car_rental_android.engine.services.LoginService
import com.andrei.car_rental_android.state.LocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

interface LoginRepository {
    fun login(loginRequest: LoginRequest):Flow<RequestState<LoginResponse>>
}

class LoginRepositoryImpl @Inject constructor(
    private val loginService: LoginService,
    private val requestExecutor: RequestExecutor,
    private val localRepository: LocalRepository
): LoginRepository {

    override fun login(loginRequest: LoginRequest):Flow<RequestState<LoginResponse>> = requestExecutor.performRequest{
        loginService.login(loginRequest)
    }.transform {requestState->
        emit(requestState)
        if(requestState is RequestState.Success){
            localRepository.setAccessToken(requestState.data.accessToken)
            localRepository.setRefreshToken(requestState.data.refreshToken)
        }
    }

}