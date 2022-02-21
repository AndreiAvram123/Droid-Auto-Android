package com.andrei.car_rental_android.navigation

sealed class Screen(val route:String){
    object HomeScreen: Screen("HomeScreen")
    object SignInScreen: Screen("SignInScreen")

    sealed class RegistrationScreen(screenName:String) : Screen(screenName){
        object NamesScreen: RegistrationScreen("NamesScreen")
        object RegisterEmail:RegistrationScreen(RegisterEmailNavHelper.route)
        object PasswordScreen:RegistrationScreen("PasswordScreen")
    }
}

