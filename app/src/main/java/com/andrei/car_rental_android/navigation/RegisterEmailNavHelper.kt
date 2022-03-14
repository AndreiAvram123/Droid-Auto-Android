package com.andrei.car_rental_android.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument

class RegisterEmailNavHelper {

    data class RegisterEmailNavArgs(
        val firstName:String,
        val lastName:String
    )

    companion object {

        private const val baseRoute :String = "registerEmail"
        val route :String = "$baseRoute?${RegisterEmailNavArgs::firstName.name}={firstName},${RegisterEmailNavArgs::lastName.name}={lastName}"

        fun getDestination(registerEmailNavArgs: RegisterEmailNavArgs):String = "$baseRoute"+
                    "?${RegisterEmailNavArgs::firstName.name}=${registerEmailNavArgs.firstName}," +
                    "${RegisterEmailNavArgs::lastName.name}=${registerEmailNavArgs.lastName}"

        fun getArguments():List<NamedNavArgument> {
            return listOf(
                navArgument(RegisterEmailNavArgs::firstName.name){
                    type = NavType.StringType
                },
                navArgument(RegisterEmailNavArgs::lastName.name){
                    type = NavType.StringType
                },
            )
        }

        fun parseArguments(backStackEntry: NavBackStackEntry): RegisterEmailNavArgs = RegisterEmailNavArgs(
                firstName = backStackEntry.arguments?.getString(RegisterEmailNavArgs::firstName.name) ?: "",
                lastName = backStackEntry.arguments?.getString(RegisterEmailNavArgs::lastName.name) ?: ""
            )
    }
}