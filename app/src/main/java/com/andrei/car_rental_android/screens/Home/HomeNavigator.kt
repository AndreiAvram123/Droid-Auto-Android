package com.andrei.car_rental_android.screens.Home

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.andrei.car_rental_android.navigation.Screen

interface HomeNavigator {
  fun navigateToOngoingRide()
}

class HomeNavigatorImpl(
    private val navController: NavController
):HomeNavigator{
    override fun navigateToOngoingRide() {
        navController.navigate(Screen.RideScreen.route){
            popUpTo(navController.graph.findStartDestination().id){
                inclusive = true
            }
        }
    }

}