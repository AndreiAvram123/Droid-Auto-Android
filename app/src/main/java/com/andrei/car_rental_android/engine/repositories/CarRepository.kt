package com.andrei.car_rental_android.engine.repositories

import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.engine.configuration.RequestExecutor
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.services.CarService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface CarRepository{
    fun fetchNearby(currentLocation:LatLng):Flow<RequestState<List<Car>>>
}

class CarRepositoryImpl @Inject constructor(
    private val requestExecutor: RequestExecutor,
    private val carService: CarService
): CarRepository {

    override fun fetchNearby(currentLocation: LatLng): Flow<RequestState<List<Car>>>  = requestExecutor.performRequest {
        carService.getNearbyCars()
    }

}