package com.andrei.car_rental_android.screens.register.creatingAccount

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.screens.register.base.RegisterScreenSurface


@Composable
fun CreatingAccountScreen(
    navController: NavController,
    args:CreatingAccountNavHelper.CreatingAccountNavArgs
){
    RegisterScreenSurface {
        Loading()
    }
}

@Composable
private fun Loading(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    LottieAnimation(
        modifier = modifier.fillMaxSize(),
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}