package com.andrei.car_rental_android.screens.Home.useCases

import com.andrei.car_rental_android.engine.repositories.CarRepository
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.screens.Home.states.CarReservationState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class CancelReservationUseCase @Inject constructor(
    private val carRepository: CarRepository
){
    operator fun invoke():Flow<CarReservationState> = carRepository.cancelCurrentReservation().transform{
        when(it){
            is RequestState.Success -> emit(CarReservationState.Default)
            else ->{

            }
        }
    }
}