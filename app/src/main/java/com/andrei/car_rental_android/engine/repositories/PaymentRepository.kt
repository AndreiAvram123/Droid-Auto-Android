package com.andrei.car_rental_android.engine.repositories

import com.andrei.car_rental_android.DTOs.PaymentResponse
import com.andrei.car_rental_android.engine.configuration.RequestExecutor
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.services.PaymentService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface PaymentRepository {
    fun makeUnlockFeePayment(): Flow<RequestState<PaymentResponse>>
}

class PaymentRepositoryImpl @Inject constructor(
    private val requestExecutor: RequestExecutor,
    private val paymentService: PaymentService
):PaymentRepository{

    override fun makeUnlockFeePayment(): Flow<RequestState<PaymentResponse>> = requestExecutor.performRequest{
        paymentService.makeUnlockFeePayment()
    }

}