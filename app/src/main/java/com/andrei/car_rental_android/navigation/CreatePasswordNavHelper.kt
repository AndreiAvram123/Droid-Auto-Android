package com.andrei.car_rental_android.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument

class CreatePasswordNavHelper {
    data class CreatePasswordNavArgs(
        val firstName:String,
        val lastName:String,
        val email:String
    )
    companion object{

        private const val routePrefix = "createPassword"

        val route :String = routePrefix + "?${CreatePasswordNavArgs::firstName.name}={firstName}," +
                     "${CreatePasswordNavArgs::lastName.name}={lastName}," +
                     "${CreatePasswordNavArgs::email.name}={email}"

        fun getDestination(createPasswordNavArgs: CreatePasswordNavArgs):String =  routePrefix +
                "?${CreatePasswordNavArgs::firstName.name}=${createPasswordNavArgs.firstName}," +
                "${CreatePasswordNavArgs::lastName.name}=${createPasswordNavArgs.lastName}," +
                "${CreatePasswordNavArgs::email.name}=${createPasswordNavArgs.email}"

        fun getArguments():List<NamedNavArgument> {
            return listOf(
                navArgument(CreatePasswordNavArgs::firstName.name){
                    type = NavType.StringType
                },
                navArgument(CreatePasswordNavArgs::lastName.name){
                    type = NavType.StringType
                },
                navArgument(CreatePasswordNavArgs::email.name){
                    type = NavType.StringType
                }
            )
        }

        fun parseArguments(backStackEntry: NavBackStackEntry): CreatePasswordNavArgs =
            CreatePasswordNavArgs(
                firstName = backStackEntry.arguments?.getString(CreatePasswordNavArgs::firstName.name)
                    ?: "",
                lastName = backStackEntry.arguments?.getString(CreatePasswordNavArgs::lastName.name)
                    ?: "",
                email = backStackEntry.arguments?.getString(CreatePasswordNavArgs::email.name) ?: ""
            )
    }
}