package com.andrei.car_rental_android.screens.register.firstNameLastName

import androidx.navigation.NavController
import com.andrei.car_rental_android.screens.register.Email.RegisterEmailNavHelper

interface FirstNameLastNameNavigator {
    fun navigateToPasswordScreen(firstName:String,lastName:String)
    fun navigateBack()
}

class FirstNameLastNameNavigatorImpl(
    private val navController: NavController
): FirstNameLastNameNavigator {

    override fun navigateToPasswordScreen(firstName: String, lastName: String) {
        navController.navigate(
            RegisterEmailNavHelper.getDestination(
                RegisterEmailNavHelper.RegisterEmailNavArgs(
                    firstName = firstName,
                    lastName = lastName
                )
            )
        )
    }

    override fun navigateBack() {
        navController.popBackStack()
    }

}
