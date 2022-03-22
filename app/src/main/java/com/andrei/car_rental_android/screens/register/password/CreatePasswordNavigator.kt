package com.andrei.car_rental_android.screens.register.password

import androidx.navigation.NavController
import com.andrei.car_rental_android.navigation.CreatePasswordNavHelper

interface CreatePasswordNavigator {
   fun  navigateToCreatingAccountScreen(password: String)
   fun navigateBack()
}

class CreatePasswordNavigatorImpl(
    private val navController: NavController,
    private val navArgs: CreatePasswordNavHelper.CreatePasswordNavArgs
):CreatePasswordNavigator{

    override fun navigateToCreatingAccountScreen(password:String) {

    }

    override fun navigateBack() {
        navController.popBackStack()
    }

}