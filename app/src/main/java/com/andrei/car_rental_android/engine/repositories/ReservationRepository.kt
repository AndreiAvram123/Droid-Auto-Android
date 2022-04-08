package com.andrei.car_rental_android.engine.repositories

import com.andrei.car_rental_android.DTOs.Reservation
import com.andrei.car_rental_android.engine.configuration.RequestExecutor
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.response.ReservationRequest
import com.andrei.car_rental_android.engine.services.ReservationService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ReservationRepository {
    fun makeReservation(reservationRequest: ReservationRequest): Flow<RequestState<Nothing>>
    fun cancelCurrentReservation(): Flow<RequestState<Nothing>>
    fun getCurrentReservation():Flow<RequestState<Reservation?>>
}

class ReservationRepositoryIml @Inject constructor(
    private val requestExecutor: RequestExecutor,
    private val reservationService: ReservationService

): ReservationRepository{


    override fun makeReservation(reservationRequest: ReservationRequest): Flow<RequestState<Nothing>> = requestExecutor.performRequest {
        reservationService.makeReservation(reservationRequest)
    }

    override fun cancelCurrentReservation(): Flow<RequestState<Nothing>> = requestExecutor.performRequest {
         reservationService.cancelReservation()
    }

    override fun getCurrentReservation(): Flow<RequestState<Reservation?>> = requestExecutor.performRequest {
        reservationService.getCurrentReservation()
    }


}
