package com.andrei.car_rental_android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.andrei.car_rental_android.screens.Home.HomeScreen
import com.andrei.car_rental_android.screens.SignIn.SignInScreen
import com.andrei.car_rental_android.screens.register.Email.RegisterEmailNavHelper
import com.andrei.car_rental_android.screens.register.Email.RegisterEmailScreen
import com.andrei.car_rental_android.screens.register.FirstNameLastNameScreen
import com.andrei.car_rental_android.screens.register.creatingAccount.CreatingAccountNavHelper
import com.andrei.car_rental_android.screens.register.creatingAccount.CreatingAccountScreen
import com.andrei.car_rental_android.screens.register.password.CreatePasswordNavHelper
import com.andrei.car_rental_android.screens.register.password.CreatePasswordScreen
import com.andrei.car_rental_android.state.LoginStateViewModel
import com.andrei.car_rental_android.state.LoginStateViewModelImpl
import com.andrei.car_rental_android.state.SessionManager


@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val loginStateViewModel = hiltViewModel<LoginStateViewModelImpl>()

    NavHost(
        navController = navController,
        startDestination = Screen.SignInScreen.route
    ) {
        composable(route = Screen.SignInScreen.route) {
            SignInScreen(navController)
        }
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController)
        }
        registerGraph(navController)
    }

    LoginState(
        navController = navController,
        loginStateViewModel = loginStateViewModel
    )
}
fun NavGraphBuilder.registerGraph(navController:NavController) {
    //register navigation
    navigation(
        startDestination = Screen.RegistrationScreen.NamesScreen.route,
        route = "register"
    ) {

        composable(route = Screen.RegistrationScreen.NamesScreen.route) {
            FirstNameLastNameScreen(navController)
        }
        composable(
            route = Screen.RegistrationScreen.RegisterEmail.route,
            arguments = RegisterEmailNavHelper.getArguments()
        ) { backStack -> RegisterEmailScreen(navController, RegisterEmailNavHelper.parseArguments(backStack))
        }
        composable(
            route = Screen.RegistrationScreen.PasswordScreen.route,
            arguments = CreatePasswordNavHelper.getArguments()
        ) {
            backStack -> CreatePasswordScreen(navController, CreatePasswordNavHelper.parseArguments(backStack))
        }
        composable(
            route = Screen.RegistrationScreen.CreatingAccount.route,
            arguments = CreatingAccountNavHelper.getArguments()
        ){
             CreatingAccountScreen(navController)
        }

    }
}


@Composable
private fun LoginState(
    navController: NavController,
    loginStateViewModel: LoginStateViewModel
) {
    when (loginStateViewModel.authenticationState.collectAsState().value) {
        SessionManager.AuthenticationState.Authenticated.AllDetailsVerified -> {
            navController.navigate(Screen.HomeScreen.route) {
                popUpTo(Screen.SignInScreen.route) {
                    inclusive = true
                }
            }
        }

        SessionManager.AuthenticationState.Authenticated.RequireEmailVerification -> {

        }
        SessionManager.AuthenticationState.Authenticated.RequireIdentityVerification -> {

        }
        SessionManager.AuthenticationState.Authenticating -> {
            //no action required
        }
        SessionManager.AuthenticationState.NotAuthenticated -> {
            //usually because refresh token expired
            navController.navigate(Screen.SignInScreen.route) {
                launchSingleTop = true
                popUpTo(0)
            }
        }
            SessionManager.AuthenticationState.Authenticated.CannotVerifyDetails -> {
                //should be an error screen
            }
        }
    }