package com.andrei.car_rental_android.screens.register.creatingAccount

import androidx.navigation.NavController
import com.andrei.car_rental_android.navigation.Screen

interface CreatingAccountNavigator {
    fun navigateToSignIn()
}

class CreatingAccountNavigatorImpl(
    private val navController: NavController
):CreatingAccountNavigator{

    override fun navigateToSignIn() {
        navController.navigate(Screen.SignInScreen.route){
            popUpTo(Screen.SignInScreen.route){
                inclusive = true
            }
        }
    }

}