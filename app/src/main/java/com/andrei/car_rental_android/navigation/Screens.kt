package com.andrei.car_rental_android.navigation

sealed class Screen(val screenName:String){
    object HomeScreen: Screen("HomeScreen")
    object SignInScreen: Screen("SignInScreen")
    object UserNameScreen: Screen("Username")
}

sealed class RegistrationScreen(screenName:String) : Screen(screenName){
    object NamesScreen: RegistrationScreen("NamesScreen")
    object EmailScreen:RegistrationScreen("EmailScreen")
}