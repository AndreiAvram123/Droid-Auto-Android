package com.andrei.car_rental_android.screens.spash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.andrei.car_rental_android.R


@Composable
fun SplashScreen(onFinished:()->Unit){
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splah))
    val progress by animateLottieCompositionAsState(composition = composition)
    val isFinished: State<Boolean> = remember {
        derivedStateOf{
            progress == 1f
        }
    }
    if(isFinished.value){
        onFinished()
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        LottieAnimation(
            composition = composition 
        )
    }
}
