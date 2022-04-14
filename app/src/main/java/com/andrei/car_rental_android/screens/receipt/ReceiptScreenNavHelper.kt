package com.andrei.car_rental_android.screens.receipt

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

class ReceiptScreenNavHelper {

    data class Args(
        val rideID: Long
    )

    companion object {
         private const val baseRoute = "receiptScreen"
         private fun String.replaceArgumentValue(name:String, value:String) = this.replace("{$name}", value)


         val route = "${baseRoute}?${Args::rideID.name}={${Args::rideID.name}}"

        fun getDestination(args:Args):String= route.replaceArgumentValue(Args::rideID.name,args.rideID.toString())

        fun getArguments():List<NamedNavArgument> = listOf(
            navArgument(Args::rideID.name){
                type = NavType.LongType
            }
        )

        fun parseArguments(savedStateHandle: SavedStateHandle):Args = Args(
             rideID = savedStateHandle.get<Long>(Args::rideID.name)?: 0
        )
    }
}