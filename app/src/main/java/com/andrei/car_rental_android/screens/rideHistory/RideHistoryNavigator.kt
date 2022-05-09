package com.andrei.car_rental_android.screens.rideHistory

import androidx.navigation.NavController
import com.andrei.car_rental_android.DTOs.FinishedRide
import com.andrei.car_rental_android.screens.finishedRide.FinishedRideNavHelper
import com.andrei.car_rental_android.ui.utils.navigateSafely

interface RideHistoryNavigator {
    fun navigateToFinishedRideScreen(finishedRide: FinishedRide)
    fun navigateBack()
}

class RideHistoryNavigatorImpl(
    private val navController: NavController
) : RideHistoryNavigator{

    override fun navigateToFinishedRideScreen(finishedRide: FinishedRide) {
        navController.navigateSafely(
            FinishedRideNavHelper.getDestination(
                FinishedRideNavHelper.Args(
                    rideID = finishedRide.id,
                    shouldNavigateHome = false
                )
            )
        )
    }

    override fun navigateBack() {
        navController.popBackStack()
    }

}