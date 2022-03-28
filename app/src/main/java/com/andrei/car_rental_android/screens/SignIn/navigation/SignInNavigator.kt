package com.andrei.car_rental_android.screens.SignIn.navigation

import androidx.navigation.NavController

interface SignInNavigator {
     fun navigateToRegister()
}

class SignInNavigatorImpl(
    private val navController: NavController
) : SignInNavigator{

    override fun navigateToRegister() {
        navController.navigate("register")
    }

}