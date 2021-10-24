package com.andrei.car_rental_android.navigation

sealed class Screen(val screenName:String){
    object HomeScreen: Screen("HomeScreen")
    object SignInScreen: Screen("SignInScreen")
}
