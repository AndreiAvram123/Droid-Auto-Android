package com.andrei.car_rental_android.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController


@Composable
fun RegisterEmailScreen(navController: NavController){
    Column(modifier = Modifier.fillMaxSize()) {
        MainContent()
    }
}

@Composable
@Preview
private fun MainContent(){
    Box(modifier = Modifier.background(MaterialTheme.colors.surface)) {

    }
}
