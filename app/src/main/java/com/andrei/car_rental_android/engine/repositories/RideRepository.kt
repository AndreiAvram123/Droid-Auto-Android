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
    fun getRideByID(id:Long):Flow<RequestState<FinishedRide>>
    fun finishOngoingRide():Flow<RequestState<FinishedRide>>
    fun getRideHistory():Flow<RequestState<List<FinishedRide>>>
    fun startRide():Flow<RequestState<OngoingRide>>
}

class RideRepositoryImpl @Inject constructor(
    private val requestExecutor: RequestExecutor,
    private val rideService: RideService
):RideRepository{

    override fun getOngoingRide(): Flow<RequestState<OngoingRide?>> = requestExecutor.performRequest {
        rideService.getOngoingRide()
    }

    override fun getRideByID(id: Long): Flow<RequestState<FinishedRide>> = requestExecutor.performRequest {
        rideService.getRideByID(id)
    }

    override fun finishOngoingRide(): Flow<RequestState<FinishedRide>> =  requestExecutor.performRequest{
       rideService.finishOngoingRide()
    }

    override fun getRideHistory(): Flow<RequestState<List<FinishedRide>>>  = requestExecutor.performRequest{
        rideService.getRideHistory()
    }

    override fun startRide(): Flow<RequestState<OngoingRide>> = requestExecutor.performRequest{
        rideService.startRide()
    }

}