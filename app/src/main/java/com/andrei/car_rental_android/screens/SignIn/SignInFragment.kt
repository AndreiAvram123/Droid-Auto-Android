package com.andrei.car_rental_android.screens.SignIn

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.andrei.car_rental_android.baseConfig.BaseFragment
import com.andrei.car_rental_android.navigation.Screen
import com.andrei.car_rental_android.ui.Dimens
import dagger.hilt.android.AndroidEntryPoint

@Composable
fun SignInScreen(navController: NavController){
    Column(modifier = Modifier
        .padding(horizontal = 32.dp)
        .fillMaxWidth()
        .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        UsernameTextField()
        Spacer(modifier = Modifier.height(Dimens.medium.dp))
        SignInButton(navController)
    }
}

@Composable
fun UsernameTextField(){
    var username:String by remember {
        mutableStateOf("")
    }
    TextField(value = username,
        onValueChange ={
            username = it
        }, modifier = Modifier
            .fillMaxWidth())
}
@Composable
fun SignInButton(navController: NavController){
    Button(onClick = {
        navController.navigate(Screen.HomeScreen.screenName)
    }) {
         Text(text = "Click me to navigate")
    }
}
