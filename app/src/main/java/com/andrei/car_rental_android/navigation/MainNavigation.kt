package com.andrei.car_rental_android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.andrei.car_rental_android.screens.Home.HomeScreen
import com.andrei.car_rental_android.screens.SignIn.SignInScreen
import com.andrei.car_rental_android.screens.register.Email.RegisterEmailScreen
import com.andrei.car_rental_android.screens.register.FirstNameLastNameScreen
import com.andrei.car_rental_android.screens.register.password.CreatePasswordScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.SignInScreen.screenName
    ) {
        composable(route = Screen.SignInScreen.screenName) {
            SignInScreen(navController)
        }
        composable(route = Screen.HomeScreen.screenName) {
            HomeScreen(navController)
        }
        registerGraph(navController)

    }

}
fun NavGraphBuilder.registerGraph(navController:NavController) {
    //register navigation
    navigation(
        startDestination = Screen.RegistrationScreen.PasswordScreen.screenName,
        route = "register"
    ) {

        composable(route = Screen.RegistrationScreen.NamesScreen.screenName) {
            FirstNameLastNameScreen(navController)
        }
        composable(route = Screen.RegistrationScreen.EmailScreen.screenName) {
            RegisterEmailScreen(navController)
        }
        composable(route = Screen.RegistrationScreen.PasswordScreen.screenName) {
            CreatePasswordScreen(navController)
        }
    }
}

sealed class Route(val name:String){
    object Register: Route("Register")
}