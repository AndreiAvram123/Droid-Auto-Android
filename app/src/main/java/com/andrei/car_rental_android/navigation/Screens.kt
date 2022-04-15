package com.andrei.car_rental_android.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.screens.finishedRide.FinishedRideNavHelper
import com.andrei.car_rental_android.screens.register.Email.RegisterEmailNavHelper
import com.andrei.car_rental_android.screens.register.creatingAccount.CreatingAccountNavHelper
import com.andrei.car_rental_android.screens.register.password.CreatePasswordNavHelper

sealed class Screen(val route:String){
    object SignInScreen: Screen("SignIn")
    object RideScreen:Screen("RideScreen")
    object ReceiptScreen:Screen(FinishedRideNavHelper.route)


    sealed class RegistrationScreen(screenName:String) : Screen(screenName){
        object NamesScreen: RegistrationScreen("Names")
        object RegisterEmail:RegistrationScreen(RegisterEmailNavHelper.route)
        object PasswordScreen:RegistrationScreen(CreatePasswordNavHelper.route)
        object CreatingAccount:RegistrationScreen(CreatingAccountNavHelper.route)
    }
}

sealed class BottomNavigationScreen(
     route: String,
     @StringRes val resourceID:Int,
     val imageVector: ImageVector
):Screen(route){
    object HomeScreen: BottomNavigationScreen("Home", R.string.home, Icons.Filled.Home)
    object Settings: BottomNavigationScreen("Settings", R.string.settings, Icons.Filled.Settings )
    object RideHistoryScreen:BottomNavigationScreen("RideHistory",R.string.ride_history, Icons.Filled.History )
}


