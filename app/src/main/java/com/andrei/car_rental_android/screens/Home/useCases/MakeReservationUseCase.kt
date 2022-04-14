package com.andrei.car_rental_android.screens.Home.useCases

import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.toAndroidLocation
import com.andrei.car_rental_android.engine.repositories.ReservationRepository
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.response.ReservationRequest
import com.andrei.car_rental_android.screens.Home.states.SelectedCarState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class MakeReservationUseCase @Inject constructor(
    private val reservationRepository: ReservationRepository
){
    operator fun invoke(car:Car):Flow<SelectedCarState> = reservationRepository.makeReservation(
        ReservationRequest(car.id)
    ).transform { when(it) {
            is RequestState.Success -> emit(SelectedCarState.Reserved(
                car = car,
                remainingTime = it.data.remainingTime.seconds,
                location = it.data.carLocation.toAndroidLocation()
            ))
            is RequestState.Loading -> emit(SelectedCarState.InProgress)
            is RequestState.Error -> {
                if(it.code == 409){
                     emit(SelectedCarState.NotAvailable)
                }else{
                    emit(SelectedCarState.Error)
                }
            }
            else -> emit(SelectedCarState.Error)
        }
    }
}