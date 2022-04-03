package com.andrei.car_rental_android.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.andrei.car_rental_android.screens.Home.HomeScreen
import com.andrei.car_rental_android.screens.Settings.SettingsScreen
import com.andrei.car_rental_android.screens.SignIn.SignInScreen
import com.andrei.car_rental_android.screens.register.Email.RegisterEmailNavHelper
import com.andrei.car_rental_android.screens.register.Email.RegisterEmailScreen
import com.andrei.car_rental_android.screens.register.FirstNameLastNameScreen
import com.andrei.car_rental_android.screens.register.creatingAccount.CreatingAccountNavHelper
import com.andrei.car_rental_android.screens.register.creatingAccount.CreatingAccountScreen
import com.andrei.car_rental_android.screens.register.password.CreatePasswordNavHelper
import com.andrei.car_rental_android.screens.register.password.CreatePasswordScreen

sealed class NavGraph{
    object MainGraph:NavGraph()
    object LoginGraph:NavGraph()
}


@Composable
fun Navigation(
     graph: NavGraph
) {
    val navController = rememberNavController()
    when(graph){
        is NavGraph.MainGraph -> {
            AppBottomNavigation(
                navHostController = navController
            ){
                MainGraph(
                    navController = navController,
                    modifier = it
                )
            }
        }
        is NavGraph.LoginGraph ->{
            LoginGraph(
                navController = navController
            )
        }
    }


}


@Composable
private fun MainGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController
){
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = BottomNavigationScreen.HomeScreen.route
    ) {

        composable(route = BottomNavigationScreen.Settings.route){
            SettingsScreen()
        }
        composable(route = BottomNavigationScreen.HomeScreen.route) {
            HomeScreen(navController)
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
private fun AppBottomNavigation(
    navHostController: NavHostController,
    mainScreenNavigation: @Composable (modifier:Modifier)->Unit
) {
    val bottomNavigationItems = listOf(
        BottomNavigationScreen.HomeScreen,
        BottomNavigationScreen.Settings,
    )
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavigationItems.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.imageVector, contentDescription = null) },
                        label = { Text(stringResource(screen.resourceID)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navHostController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navHostController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding -> mainScreenNavigation(Modifier.padding(innerPadding))
    }
}

