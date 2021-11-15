package com.andrei.car_rental_android.screens.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.composables.TextFieldLabel
import com.andrei.car_rental_android.screens.register.Email.RegisterEmailViewModel
import com.andrei.car_rental_android.screens.register.Email.RegisterEmailViewModelImpl
import com.andrei.car_rental_android.screens.register.base.RegisterScreenSurface


@Composable
fun RegisterEmailScreen(navController: NavController){
    Column(modifier = Modifier.fillMaxSize()) {
        MainContent()
    }
}

@Composable
@Preview
private fun MainContent(onNavigateForward : ()-> Unit = {}){
    val viewModel:RegisterEmailViewModel = hiltViewModel<RegisterEmailViewModelImpl>()
    RegisterScreenSurface {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            EmailTextField(
                modifier = Modifier.fillMaxWidth(),
                state = viewModel.email.collectAsState()
                , onValueChanged ={
                    viewModel.setEmail(it)
                } )
        }
    }
}

@Composable
private fun EmailTextField(
    modifier: Modifier = Modifier,
    state: State<String>,
    onValueChanged: (newValue:String)-> Unit){
    OutlinedTextField(
        modifier = modifier,
        value = state.value,
        onValueChange = {
            onValueChanged(it)
        },
        placeholder = {
            TextFieldLabel(text = stringResource(R.string.screen_email_email))
        }
    )
}




