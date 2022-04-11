package com.andrei.car_rental_android.screens.ride

import androidx.navigation.NavController
import com.andrei.car_rental_android.navigation.Screen

sealed interface RideNavigator{
   fun navigateToReceiptScreen()
}

class RideNavigatorImpl(
    private val navController: NavController
):RideNavigator{
    override fun navigateToReceiptScreen() {
        navController.navigate(Screen.ReceiptScreen.route){
            popUpTo(Screen.RideScreen.route){
                inclusive = true
            }
        }

    }

}
