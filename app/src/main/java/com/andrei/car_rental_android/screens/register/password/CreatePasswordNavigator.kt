package com.andrei.car_rental_android.screens.register.password

import androidx.navigation.NavController
import com.andrei.car_rental_android.screens.register.creatingAccount.CreatingAccountNavHelper

interface CreatePasswordNavigator {
   fun  navigateToCreatingAccountScreen(password: String)
    fun navigateBack()
}

class CreatePasswordNavigatorImpl(
    private val navController: NavController,
    private val args: CreatePasswordNavHelper.CreatePasswordNavArgs
):CreatePasswordNavigator{

    override fun navigateToCreatingAccountScreen(password:String) {
       navController.navigate(
           CreatingAccountNavHelper.getDestination(
               CreatingAccountNavHelper.CreatingAccountNavArgs(
                   firstName = args.firstName,
                   lastName = args.lastName,
                   email = args.email,
                   password = password
               )
           )
       )
    }

    override fun navigateBack() {
        navController.popBackStack()
    }

}