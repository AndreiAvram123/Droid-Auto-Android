package com.andrei.car_rental_android.engine.repositories

import com.andrei.car_rental_android.DTOs.FinishedRide
import com.andrei.car_rental_android.DTOs.OngoingRide
import com.andrei.car_rental_android.engine.configuration.RequestExecutor
import com.andrei.car_rental_android.engine.request.RequestState
import com.andrei.car_rental_android.engine.services.RideService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface RideRepository {
    fun getOngoingRide(): Flow<RequestState<OngoingRide?>>
    fun finishOngoingRide():Flow<RequestState<FinishedRide>>
}

class RideRepositoryImpl @Inject constructor(
    private val requestExecutor: RequestExecutor,
    private val rideService: RideService
):RideRepository{

    override fun getOngoingRide(): Flow<RequestState<OngoingRide?>> = requestExecutor.performRequest {
        rideService.getOngoingRide()
    }

    override fun finishOngoingRide(): Flow<RequestState<FinishedRide>> =  requestExecutor.performRequest{
       rideService.finishOngoingRide()
    }

}