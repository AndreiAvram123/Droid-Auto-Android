package com.andrei.car_rental_android.engine

import com.andrei.car_rental_android.engine.services.LoginService
import javax.inject.Inject

interface LoginRepository {
    fun login()
}

class LoginRepositoryImpl @Inject constructor(
    private val loginService: LoginService
):LoginRepository{
    override fun login() {
        TODO("Not yet implemented")
    }

}