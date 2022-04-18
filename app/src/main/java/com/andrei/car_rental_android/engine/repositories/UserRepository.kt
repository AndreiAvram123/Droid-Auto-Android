package com.andrei.car_rental_android.engine.repositories

import com.andrei.car_rental_android.DTOs.User
import com.andrei.car_rental_android.engine.configuration.RequestExecutor
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.services.UserService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserRepository {
    fun getCurrentUser(): Flow<RequestState<User>>
}

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val requestExecutor: RequestExecutor
):UserRepository{
    override fun getCurrentUser(): Flow<RequestState<User>> = requestExecutor.performRequest {
        userService.getCurrentUser()
    }


}