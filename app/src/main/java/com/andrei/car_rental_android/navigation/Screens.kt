package com.andrei.car_rental_android.navigation

import com.andrei.car_rental_android.screens.register.Email.RegisterEmailNavHelper
import com.andrei.car_rental_android.screens.register.creatingAccount.CreatingAccountNavHelper
import com.andrei.car_rental_android.screens.register.password.CreatePasswordNavHelper

sealed class Screen(val route:String){
    object HomeScreen: Screen("HomeScreen")
    object SignInScreen: Screen("SignInScreen")

    sealed class RegistrationScreen(screenName:String) : Screen(screenName){
        object NamesScreen: RegistrationScreen("NamesScreen")
        object RegisterEmail:RegistrationScreen(RegisterEmailNavHelper.route)
        object PasswordScreen:RegistrationScreen(CreatePasswordNavHelper.route)
        object CreatingAccount:RegistrationScreen(CreatingAccountNavHelper.route)
    }
}

