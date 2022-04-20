package com.andrei.car_rental_android.engine.repositories

import android.location.Location
import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.CarWithLocation
import com.andrei.car_rental_android.DTOs.OngoingRide
import com.andrei.car_rental_android.engine.configuration.RequestExecutor
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.services.CarService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface CarRepository{
    fun getNearbyCars(location:Location):Flow<RequestState<List<CarWithLocation>>>
    fun unlockCar():Flow<RequestState<OngoingRide>>
    fun lockCar():Flow<RequestState<Nothing>>
    fun getCarLocation(car:Car):Flow<RequestState<LatLng?>>
}

class CarRepositoryImpl @Inject constructor(
    private val requestExecutor: RequestExecutor,
    private val carService: CarService
): CarRepository {

    override fun getNearbyCars(location: Location): Flow<RequestState<List<CarWithLocation>>>  = requestExecutor.performRequest {
        carService.getNearbyCars(
            latitude = location.latitude,
            longitude = location.longitude
        )
    }

    override fun unlockCar(): Flow<RequestState<OngoingRide>> = requestExecutor.performRequest {
       return@performRequest carService.unlockCar()
    }

    override fun lockCar(): Flow<RequestState<Nothing>>  = requestExecutor.performRequest{
        carService.lockCar()
    }

    override fun getCarLocation(car:Car): Flow<RequestState<LatLng?>> = requestExecutor.performRequest{
        return@performRequest carService.getCarLocation(car.id)
    }

}