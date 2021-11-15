package com.andrei.car_rental_android.engine.repositories

import com.andrei.car_rental_android.engine.configuration.RequestExecutor
import com.andrei.car_rental_android.engine.configuration.RequestState
import com.andrei.car_rental_android.engine.request.LoginRequest
import com.andrei.car_rental_android.engine.response.LoginResponse
import com.andrei.car_rental_android.engine.services.LoginService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LoginRepository {
    fun login(loginRequest: LoginRequest):Flow<RequestState<LoginResponse>>
}

class LoginRepositoryImpl @Inject constructor(
    private val loginService: LoginService,
    private val requestExecutor: RequestExecutor
): LoginRepository {

    override fun login(loginRequest: LoginRequest):Flow<RequestState<LoginResponse>> =  requestExecutor.performRequest{
       loginService.login(loginRequest)
    }

}