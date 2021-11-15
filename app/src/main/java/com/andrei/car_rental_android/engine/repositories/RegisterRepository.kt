package com.andrei.car_rental_android.engine.repositories

import com.andrei.car_rental_android.engine.configuration.RequestExecutor
import com.andrei.car_rental_android.engine.configuration.RequestState
import com.andrei.car_rental_android.engine.services.RegisterService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface RegisterRepository {
    fun checkIfEmailIsUsed(email:String): Flow<RequestState<Nothing>>

    companion object{
        const val errorEmailUsed = "Email already used"
    }
}

class RegisterRepositoryImpl @Inject constructor(
    private val requestExecutor: RequestExecutor,
    private val registerService: RegisterService
) : RegisterRepository{

    override fun checkIfEmailIsUsed(email: String): Flow<RequestState<Nothing>>  = requestExecutor.performRequest{
         registerService.checkIfEmailIsUsed(email)
    }


}