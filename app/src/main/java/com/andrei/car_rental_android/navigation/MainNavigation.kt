package com.andrei.car_rental_android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.andrei.car_rental_android.screens.Home.HomeScreen
import com.andrei.car_rental_android.screens.SignIn.SignInScreen

@Composable
fun MainNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.SignInScreen.screenName){
        composable(route = Screen.SignInScreen.screenName) {
            SignInScreen(navController)
        }
        composable(route = Screen.HomeScreen.screenName){
            HomeScreen(navController)
        }
    }
}
