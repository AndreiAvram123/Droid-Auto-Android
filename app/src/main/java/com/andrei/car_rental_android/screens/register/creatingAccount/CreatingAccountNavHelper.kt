package com.andrei.car_rental_android.screens.register.creatingAccount

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument

class CreatingAccountNavHelper {

    data class CreatingAccountNavArgs(
         val firstName:String,
         val lastName:String,
         val email:String,
         val password:String
    )

    companion object{

        private const val baseRoute :String = "createAccount"
        val route :String = "${baseRoute}?" +
        "${CreatingAccountNavArgs::firstName. name}={firstName}," +
        "${CreatingAccountNavArgs::lastName}={lastName}," +
        "${CreatingAccountNavArgs::email}={email}," +
        "${CreatingAccountNavArgs::password}={password}"


        fun getDestination(args:CreatingAccountNavArgs):String = "${baseRoute}?" +
                "${CreatingAccountNavArgs::firstName. name}=${args.firstName}," +
                "${CreatingAccountNavArgs::lastName}=${args.lastName}," +
                "${CreatingAccountNavArgs::email}=${args.email}," +
                "${CreatingAccountNavArgs::password}=${args.password}"

        fun getArguments():List<NamedNavArgument> {
            return listOf(
                navArgument(CreatingAccountNavArgs::firstName.name){
                    type = NavType.StringType
                },
                navArgument(CreatingAccountNavArgs::lastName.name){
                    type = NavType.StringType
                },
                navArgument(CreatingAccountNavArgs::email.name){
                    type = NavType.StringType
                },
                navArgument(CreatingAccountNavArgs::password.name){
                    type = NavType.StringType
                }
            )
        }

        fun parseArguments(backStackEntry: NavBackStackEntry): CreatingAccountNavArgs =
            CreatingAccountNavArgs(
                firstName = backStackEntry.arguments!!.getString(CreatingAccountNavArgs::firstName.name)
                    ?: "",
                lastName = backStackEntry.arguments?.getString(CreatingAccountNavArgs::lastName.name)
                    ?: "",
                email = backStackEntry.arguments?.getString(CreatingAccountNavArgs::email.name)
                    ?: "",
                password = backStackEntry.arguments?.getString(CreatingAccountNavArgs::password.name)
                    ?: ""
            )
    }
}