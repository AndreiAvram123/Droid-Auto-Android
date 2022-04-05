package com.andrei.car_rental_android.engine.repositories

import android.location.Location
import com.andrei.car_rental_android.engine.configuration.RequestExecutor
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.response.DirectionsResponse
import com.andrei.car_rental_android.engine.services.DirectionsService
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject

interface DirectionsRepository {
  fun getDirections(
      startLocation :Location,
      endLocation: Location
  ):Flow<RequestState<DirectionsResponse>>
}

class DirectionsRepositoryImpl @Inject constructor(
    private val requestExecutor: RequestExecutor,
    private val directionsService: DirectionsService
):DirectionsRepository{

    override fun getDirections(startLocation: Location, endLocation: Location):Flow<RequestState<DirectionsResponse>> = requestExecutor.performRequest  {
       directionsService.getDirections(
           startLatitude = startLocation.latitude,
           startLongitude = startLocation.longitude,
           endLatitude = endLocation.latitude,
           endLongitude = endLocation.longitude
       )
    }

}