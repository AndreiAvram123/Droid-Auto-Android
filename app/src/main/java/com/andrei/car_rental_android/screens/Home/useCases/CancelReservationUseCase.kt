package com.andrei.car_rental_android.screens.Home.useCases

import com.andrei.car_rental_android.engine.repositories.ReservationRepository
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.screens.Home.states.SelectedCarState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class CancelReservationUseCase @Inject constructor(
    private val reservationRepository: ReservationRepository
){
    operator fun invoke():Flow<SelectedCarState> = reservationRepository.cancelCurrentReservation().transform{
        when(it){
            is RequestState.Success -> emit(SelectedCarState.Default)
            else ->{

            }
        }
    }
}