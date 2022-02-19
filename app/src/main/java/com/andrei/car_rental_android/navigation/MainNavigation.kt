package com.andrei.car_rental_android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.andrei.car_rental_android.screens.Home.HomeScreen
import com.andrei.car_rental_android.screens.SignIn.SignInScreen
import com.andrei.car_rental_android.screens.register.Email.RegisterEmailScreen
import com.andrei.car_rental_android.screens.register.UserNameScreen

@Composable
fun MainNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.UserNameScreen.screenName){
        composable(route = Screen.SignInScreen.screenName) {
            SignInScreen(navController)
        }
        composable(route = Screen.HomeScreen.screenName){
            HomeScreen(navController)
        }
        composable(route = Screen.UserNameScreen.screenName) {
            UserNameScreen(navController)
        }
        //register navigation
        navigation(startDestination = Screen.UserNameScreen.screenName, route = "register"){
            composable(route = Screen.UserNameScreen.screenName){
                UserNameScreen(navController)
            }
            composable(route = RegistrationScreen.EmailScreen.screenName){
                RegisterEmailScreen(navController)
            }
        }
    }

}
