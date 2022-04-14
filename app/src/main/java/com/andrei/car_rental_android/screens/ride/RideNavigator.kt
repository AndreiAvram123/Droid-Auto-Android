package com.andrei.car_rental_android.screens.ride

import androidx.navigation.NavController
import com.andrei.car_rental_android.DTOs.FinishedRide
import com.andrei.car_rental_android.navigation.Screen
import com.andrei.car_rental_android.screens.receipt.ReceiptScreenNavHelper

sealed interface RideNavigator{
   fun navigateToReceiptScreen(finishedRide: FinishedRide)
}

class RideNavigatorImpl(
    private val navController: NavController
):RideNavigator{
    override fun navigateToReceiptScreen(finishedRide: FinishedRide) {
        navController.navigate(ReceiptScreenNavHelper.getDestination(
            ReceiptScreenNavHelper.Args(rideID = finishedRide.id)
        )){
            popUpTo(Screen.RideScreen.route){
                inclusive = true
            }
        }

    }

}
