package com.andrei.car_rental_android.screens.register.creatingAccount

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
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
        "${Args::firstName.name}={firstName}," +
        "${Args::lastName.name}={lastName}," +
        "${Args::email.name}={email}," +
        "${Args::password.name}={password}"


        fun getDestination(args:Args):String = "${baseRoute}?" +
                "${Args::firstName.name}=${args.firstName}," +
                "${Args::lastName.name}=${args.lastName}," +
                "${Args::email.name}=${args.email}," +
                "${Args::password.name}=${args.password}"

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

        fun parseArguments(savedStateHandle: SavedStateHandle):Args = Args(
                firstName = savedStateHandle.get<String>(Args::firstName.name)?: "",
                lastName = savedStateHandle.get<String>(Args::lastName.name)?:"",
                email = savedStateHandle.get<String>(Args::email.name) ?: "",
                password = savedStateHandle.get<String>(Args::password.name) ?: ""
            )
    }
}