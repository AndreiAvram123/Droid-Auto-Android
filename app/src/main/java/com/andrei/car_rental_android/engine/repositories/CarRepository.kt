package com.andrei.car_rental_android.engine.repositories

import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.engine.configuration.RequestExecutor
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.response.ReservationRequest
import com.andrei.car_rental_android.engine.services.CarService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface CarRepository{
    fun fetchNearby(latitude:Double,longitude:Double):Flow<RequestState<List<Car>>>
    fun makeReservation(reservationRequest: ReservationRequest):Flow<RequestState<Nothing>>
    fun cancelCurrentReservation():Flow<RequestState<Nothing>>
    fun unlockCar():Flow<RequestState<Nothing>>
}

class CarRepositoryImpl @Inject constructor(
    private val requestExecutor: RequestExecutor,
    private val carService: CarService
): CarRepository {

    override fun fetchNearby(latitude:Double, longitude: Double): Flow<RequestState<List<Car>>>  = requestExecutor.performRequest {
        carService.getNearbyCars()
    }

    override fun makeReservation(reservationRequest: ReservationRequest): Flow<RequestState<Nothing>> = requestExecutor.performRequest{
        carService.makeReservation(reservationRequest)
    }

    override fun cancelCurrentReservation(): Flow<RequestState<Nothing>>  = requestExecutor.performRequest{
        carService.cancelReservation()
    }

    override fun unlockCar(): Flow<RequestState<Nothing>> = requestExecutor.performRequest {
       carService.unlockCar()
    }

}