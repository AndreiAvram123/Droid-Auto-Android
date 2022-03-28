package com.andrei.car_rental_android.screens.register.creatingAccount

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument

class CreatingAccountNavHelper {

    data class Args(
         val firstName:String,
         val lastName:String,
         val email:String,
         val password:String
    )

    companion object{

        private const val baseRoute :String = "createAccount"
        val route :String = "${baseRoute}?" +
        "${Args::firstName. name}={firstName}," +
        "${Args::lastName}={lastName}," +
        "${Args::email}={email}," +
        "${Args::password}={password}"


        fun getDestination(args:Args):String = "${baseRoute}?" +
                "${Args::firstName. name}=${args.firstName}," +
                "${Args::lastName}=${args.lastName}," +
                "${Args::email}=${args.email}," +
                "${Args::password}=${args.password}"

        fun getArguments():List<NamedNavArgument> {
            return listOf(
                navArgument(Args::firstName.name){
                    type = NavType.StringType
                },
                navArgument(Args::lastName.name){
                    type = NavType.StringType
                },
                navArgument(Args::email.name){
                    type = NavType.StringType
                },
                navArgument(Args::password.name){
                    type = NavType.StringType
                }
            )
        }

        fun parseArguments(backStackEntry: NavBackStackEntry): Args =
            Args(
                firstName = backStackEntry.arguments!!.getString(Args::firstName.name)
                    ?: "",
                lastName = backStackEntry.arguments?.getString(Args::lastName.name)
                    ?: "",
                email = backStackEntry.arguments?.getString(Args::email.name)
                    ?: "",
                password = backStackEntry.arguments?.getString(Args::password.name)
                    ?: ""
            )
        fun parseArguments(savedStateHandle: SavedStateHandle):Args = Args(
                firstName = savedStateHandle.get<String>(Args::firstName.name)?: "",
                lastName = savedStateHandle.get<String>(Args::lastName.name)?:"",
                email = savedStateHandle.get<String>(Args::email.name) ?: "",
                password = savedStateHandle.get<String>(Args::password.name) ?: ""
            )
    }
}