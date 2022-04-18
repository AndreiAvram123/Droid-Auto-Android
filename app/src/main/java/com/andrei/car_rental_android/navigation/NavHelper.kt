package com.andrei.car_rental_android.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import com.andrei.car_rental_android.screens.finishedRide.FinishedRideNavHelper

interface NavHelper {
      fun String.replaceArgumentValue(name:String, value:String) = this.replace("{$name}", value)
     fun getDestination(args: FinishedRideNavHelper.Args):String
    fun getArguments():List<NamedNavArgument>
    fun parseArguments(savedStateHandle: SavedStateHandle): FinishedRideNavHelper.Args
}