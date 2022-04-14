package com.andrei.car_rental_android.engine.repositories

import com.andrei.car_rental_android.DTOs.CarWithLocation
import com.andrei.car_rental_android.DTOs.OngoingRide
import com.andrei.car_rental_android.engine.configuration.RequestExecutor
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.services.CarService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface CarRepository{
    fun fetchNearby(latitude:Double,longitude:Double):Flow<RequestState<List<CarWithLocation>>>
    fun unlockCar():Flow<RequestState<OngoingRide>>
}

class CarRepositoryImpl @Inject constructor(
    private val requestExecutor: RequestExecutor,
    private val carService: CarService
): CarRepository {

    override fun fetchNearby(latitude:Double, longitude: Double): Flow<RequestState<List<CarWithLocation>>>  = requestExecutor.performRequest {
        carService.getNearbyCars(
            latitude = latitude,
            longitude = longitude
        )
    }

    override fun unlockCar(): Flow<RequestState<OngoingRide>> = requestExecutor.performRequest {
       carService.unlockCar()
    }

}