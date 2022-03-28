package com.andrei.car_rental_android.screens.register.Email

import androidx.navigation.NavController
import com.andrei.car_rental_android.screens.register.password.CreatePasswordNavHelper

interface RegisterEmailNavigator {
    fun navigateBack()
    fun navigateToPasswordScreen(email:String)
}

class RegisterEmailNavigatorImpl(
    private val navController: NavController,
    private val navArgs : RegisterEmailNavHelper.RegisterEmailNavArgs
) : RegisterEmailNavigator {

    override fun navigateBack() {
        navController.popBackStack()
    }

    override fun navigateToPasswordScreen(email: String) {
        navController.navigate(
            CreatePasswordNavHelper.getDestination(
                CreatePasswordNavHelper.CreatePasswordNavArgs(
                    firstName = navArgs.firstName,
                    lastName = navArgs.lastName,
                    email = email
                )
            )
        )
    }
}