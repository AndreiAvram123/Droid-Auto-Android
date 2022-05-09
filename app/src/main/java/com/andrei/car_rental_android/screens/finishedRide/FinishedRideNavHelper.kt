package com.andrei.car_rental_android.screens.finishedRide

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.andrei.car_rental_android.navigation.NavHelper

object FinishedRideNavHelper  : NavHelper {

    data class Args(
        val rideID: Long,
        val shouldNavigateHome:Boolean
    )

    private const val baseRoute = "receiptScreen"

    val route = "${baseRoute}?${Args::rideID.name}={${Args::rideID.name}},${Args::shouldNavigateHome.name}={${Args::shouldNavigateHome.name}}"

    override fun getDestination(args:Args):String= route.replaceArgumentValue(Args::rideID.name,args.rideID.toString())
        .replaceArgumentValue(Args::shouldNavigateHome.name,args.shouldNavigateHome.toString())

    override fun getArguments():List<NamedNavArgument> = listOf(
        navArgument(Args::rideID.name){
            type = NavType.LongType
        } ,
        navArgument(Args::shouldNavigateHome.name){
            type = NavType.BoolType
        }
    )

    override fun parseArguments(savedStateHandle: SavedStateHandle):Args = Args(
        rideID = savedStateHandle.get<Long>(Args::rideID.name)?: 0,
        shouldNavigateHome = savedStateHandle.get<Boolean>(Args::shouldNavigateHome.name) ?: false
    )
}