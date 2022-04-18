package com.andrei.car_rental_android.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.andrei.car_rental_android.DTOs.User
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.state.SessionUserState
import com.andrei.car_rental_android.ui.Dimens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SideDrawer(
    navController: NavHostController,
    sessionUserStateCompose: State<SessionUserState>,
    mainScreenContent: @Composable (openDrawer:()->Unit)->Unit,
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
                scope.launch {
                    drawerState.close()
                }
            }, sessionUserStateCompose = sessionUserStateCompose
            )
        }) {
        mainScreenContent(openDrawer = {
            scope.launch {
                drawerState.open()
            }
        })
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDrawer(){
    DrawerContent(
        navigate = {},
        sessionUserStateCompose = MutableStateFlow(SessionUserState.Loaded(
            user = User(
                id = 1,
                firstName = "Andrei",
                lastName = "Avram",
                email = "avramandrei@gmail.com"
            )
        )).collectAsState())

}

@Composable
private fun DrawerContent(
    modifier: Modifier = Modifier,
    sessionUserStateCompose: State<SessionUserState>,
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
        verticalArrangement = Arrangement.spacedBy(
            Dimens.large.dp
        )
    ) {
        UserHeader(
            sessionUserStateCompose = sessionUserStateCompose
        )
        navigationItems.forEach {
            DrawerNavigationItem(
                text = stringResource(it.resourceID),
                imageVector = it.imageVector,
                onClick = {
                    navigate(it)
                }
            )
        }
    }
}


@Composable
private fun UserHeader(sessionUserStateCompose: State<SessionUserState>){
    when( val sessionUserState = sessionUserStateCompose.value){
        is SessionUserState.Loaded -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UserImage()
                Spacer(modifier = Modifier.height(Dimens.small.dp))
                FirstName(
                    firstName =  sessionUserState.user.firstName
                )
            }
        }
        is SessionUserState.LoadingUser -> {

        }
        is SessionUserState.ErrorLoadingUser -> {

        }
    }
}

@Composable
private fun FirstName(
    firstName:String
){
    Text(
        text = firstName,
        color = Color.Black,
        fontSize = Dimens.large.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun UserImage(){
    AsyncImage(
        model = "https://robohash.org/andrei_avram?set=3",
        placeholder = painterResource(R.drawable.user_placeholder),
        contentDescription = null
    )
}

@Composable
private fun DrawerNavigationItem(
    text:String,
    imageVector: ImageVector,
    onClick:()->Unit
) {
    Row(
        modifier = Modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
            color = Color.Black
        )

    }
}