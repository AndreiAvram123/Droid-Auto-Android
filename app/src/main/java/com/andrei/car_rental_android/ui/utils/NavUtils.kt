package com.andrei.car_rental_android.ui.utils

import androidx.navigation.NavController

/**
 *  This prevents  double clicks
 */
fun NavController.navigateSafely(route: String) {

    if(currentDestination?.route != route.replaceArgumentValues()){
        navigate(route)
    }
}

/**
 * A hell complicated solution
 * Basically transforms a destination string such as homeScreen?carID=4744 to a route string such as  homeScreen?carID={carID}
 *
 */
fun String.replaceArgumentValues():String{
    val regexArgumentValue = "(?<=\\w=)\\w+".toRegex()
    val regexKeyValue =  "(\\w+)(?==\\w+)".toRegex()

    var replacedString = this
    val replacementValues = regexKeyValue.findAll(this)
      replacementValues.forEachIndexed {index,match->
        val toReplace =  regexArgumentValue.findAll(this).elementAt(index).value

         replacedString =  this.replaceFirst(
             toReplace,
             "{${match.value}}"
         )
      }
    return replacedString
}