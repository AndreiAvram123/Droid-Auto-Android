package com.andrei.car_rental_android.screens.Settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.andrei.car_rental_android.R


@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Content()
    }
}

@Composable
private fun Content(){
    val viewModel: SettingsViewModel = hiltViewModel<SettingsViewModelImpl>()
    BottomSettings{
        viewModel.signOut()
    }
}

@Composable
private fun BottomSettings(
    signOut:()->Unit
){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
       SignOutButton(signOut = signOut)
    }
}

@Composable
private fun SignOutButton(
    modifier: Modifier = Modifier,
    signOut:()->Unit
){
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = signOut
    ) {
        Text(
            text = stringResource(R.string.screen_settings_sign_out)
        )
    }
}