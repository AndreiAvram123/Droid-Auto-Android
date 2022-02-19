package com.andrei.car_rental_android.engine.repositories

import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.engine.configuration.RequestState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface CarRepository{
    fun fetchNearby(currentLocation:LatLng):Flow<RequestState<List<Car>>>
}

class CarRepositoryImpl @Inject constructor(

): CarRepository {

    override fun fetchNearby(currentLocation: LatLng): Flow<RequestState<List<Car>>>  = flow {

    }

}