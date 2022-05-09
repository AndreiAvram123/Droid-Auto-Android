

package com.andrei.car_rental_android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.andrei.car_rental_android.screens.Home.HomeScreen
import com.andrei.car_rental_android.screens.Settings.SettingsScreen
import com.andrei.car_rental_android.screens.SignIn.SignInScreen
import com.andrei.car_rental_android.screens.finishedRide.FinishedRideNavHelper
import com.andrei.car_rental_android.screens.finishedRide.FinishedRideScreen
import com.andrei.car_rental_android.screens.register.Email.RegisterEmailNavHelper
import com.andrei.car_rental_android.screens.register.Email.RegisterEmailScreen
import com.andrei.car_rental_android.screens.register.NamesScreen
import com.andrei.car_rental_android.screens.register.creatingAccount.CreatingAccountNavHelper
import com.andrei.car_rental_android.screens.register.creatingAccount.CreatingAccountScreen
import com.andrei.car_rental_android.screens.register.password.CreatePasswordNavHelper
import com.andrei.car_rental_android.screens.register.password.CreatePasswordScreen
import com.andrei.car_rental_android.screens.ride.RideScreen
import com.andrei.car_rental_android.screens.rideHistory.RideHistoryScreen
import com.andrei.car_rental_android.screens.verification.DocumentVerificationScreen
import com.andrei.car_rental_android.state.SessionManager




@Composable
fun Navigation(
    currentLoginState: State<SessionManager.AuthenticationState>
) {
    val navController = rememberNavController()

    when(val loginState = currentLoginState.value){
        is SessionManager.AuthenticationState.Authenticated.IdentityVerified-> {

            SideDrawer(
                sessionUserStateCompose = loginState.sessionUserState.collectAsState(),
                navController = navController
            ){ openDrawer ->
                MainGraph(
                    openDrawer = openDrawer,
                    navController =  navController
                )
            }
        }
        is SessionManager.AuthenticationState.Authenticated.IdentifyNotVerified ->{
            DocumentVerificationScreen()
        }
        else -> {
            LoginGraph(
                navController = navController
            )
        }
    }


}


@Composable
private fun MainGraph(
    openDrawer: () -> Unit,
    navController: NavHostController
){
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {

        composable(route = DrawerNavigationScreen.Settings.route){
            SettingsScreen()
        }
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController, openDrawer)
        }
        composable(route = DrawerNavigationScreen.RideHistory.route) {
            RideHistoryScreen(navController)
        }

        composable(route = Screen.RideScreen.route){
            RideScreen(navController)
        }
        composable(
            arguments = FinishedRideNavHelper.getArguments(),
            route = Screen.ReceiptScreen.route){
            FinishedRideScreen(navController)
        }

    }
}

@Composable
private fun LoginGraph(navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination =  Screen.SignInScreen.route
    ){
        composable(route = Screen.SignInScreen.route) {
            SignInScreen(navController)
        }
        registerGraph(navController)
    }
}



fun NavGraphBuilder.registerGraph(navController:NavController) {
    //register navigation
    navigation(
        startDestination = Screen.RegistrationScreen.NamesScreen.route,
        route = "register"
    ) {

        composable(route = Screen.RegistrationScreen.NamesScreen.route) {
            NamesScreen(navController)
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



