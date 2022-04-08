package com.andrei.car_rental_android.screens.ride

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.ui.Dimens


@Composable
fun RideScreen(){

    MainContent()
}
@Composable
private fun MainContent(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.small.dp),
    ){
        TopContent{
            EnjoyYourRide()
        }
        CenterContent {
          RideOngoingAnimation()
        }
    }
}

@Composable
private fun CenterContent(
    modifier :Modifier = Modifier,
    content:@Composable ()-> Unit
){
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        content()
    }
}

@Composable
private fun RideOngoingAnimation(){
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ride_ongoing))
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}




@Composable
private fun TopContent(
    modifier:Modifier = Modifier,
    content : @Composable () -> Unit
){
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        content()
    }
}
@Composable
private fun EnjoyYourRide(
    modifier :Modifier  = Modifier
){
    Text(
        modifier = modifier.fillMaxWidth(),
        text = "Enjoy your ride!",
        fontSize = Dimens.medium.sp,
        color = Color.Black,
        textAlign = TextAlign.Center
    )

}