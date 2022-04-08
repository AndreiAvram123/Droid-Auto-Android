package com.andrei.car_rental_android.screens.Home.useCases

import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.engine.repositories.ReservationRepository
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.response.ReservationRequest
import com.andrei.car_rental_android.screens.Home.states.CarReservationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class MakeReservationUseCase @Inject constructor(
    private val reservationRepository: ReservationRepository
){
    operator fun invoke(car:Car):Flow<CarReservationState> = reservationRepository.makeReservation(
        ReservationRequest(car.id)
    ).transform {
        when(it) {
            is RequestState.Success -> emit(CarReservationState.TemporaryReserved(car))
            is RequestState.Loading -> emit(CarReservationState.InProgress)
            is RequestState.Error -> {
                if(it.code == 409){
                     emit(CarReservationState.NotAvailable)
                }else{
                    emit(CarReservationState.Error)
                }
            }
            else -> emit(CarReservationState.Error)
        }
    }
}