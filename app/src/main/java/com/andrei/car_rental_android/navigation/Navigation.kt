

package com.andrei.car_rental_android.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.andrei.car_rental_android.state.SessionManager
import com.andrei.car_rental_android.ui.Dimens
import kotlinx.coroutines.launch

sealed class NavGraph{
    object MainGraph:NavGraph()
    object LoginGraph:NavGraph()
}


@Composable
fun Navigation(
    currentLoginState: State<SessionManager.AuthenticationState>
) {
    val navController = rememberNavController()
    val graph = when (currentLoginState.value) {
        is SessionManager.AuthenticationState.Authenticated -> NavGraph.MainGraph
        else -> NavGraph.LoginGraph
    }
    when(graph){
        is NavGraph.MainGraph -> {
            SideDrawer(
                navController = navController
            ){ openDrawer ->
                MainGraph(
                    openDrawer = openDrawer,
                    navController =  navController
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SideDrawer(
    navController: NavHostController,
    content: @Composable (openDrawer:()->Unit)->Unit,
){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalDrawer(
        gesturesEnabled = drawerState.isOpen,
        drawerState = drawerState,
        drawerShape = RoundedCornerShape(0),
        drawerContent = {
            DrawerContent(navigate = {
                navController.navigate(it.route)
            })
        }) {
        content(openDrawer = {
            scope.launch {
                drawerState.open()
            }
        })
    }
}

@Preview
@Composable
private fun PreviewDrawer(){
    DrawerContent(navigate = {})
    
}

@Composable
private fun DrawerContent(
    modifier: Modifier = Modifier,
    navigate:(screen:Screen) -> Unit
){
    val navigationItems = listOf(
        DrawerNavigationScreen.RideHistory,
        DrawerNavigationScreen.Settings,

    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = Dimens.medium.dp,
                top = Dimens.medium.dp
            ),
    ) {
       navigationItems.forEach {
           DrawerNavigationItem(
               text = stringResource(it.resourceID),
               imageVector = it.imageVector,
               onClick = {
                   navigate(it)
               }
           )
           Spacer(modifier = Modifier.height(Dimens.large.dp))
       }
    }
}

@Composable
private fun DrawerNavigationItem(
    text:String,
    imageVector:ImageVector,
    onClick:()->Unit
){
    Row (
        modifier = Modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = imageVector,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(Dimens.large.dp))
        Text(
            text = text,
            fontSize = Dimens.medium.sp,
            fontWeight = FontWeight.SemiBold,
        )

    }
}


