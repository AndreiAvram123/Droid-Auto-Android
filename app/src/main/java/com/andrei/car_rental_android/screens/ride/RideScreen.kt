package com.andrei.car_rental_android.screens.ride

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
@Preview(showBackground = true, showSystemUi = true)
private fun MainContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.small.dp)
    ) {
        TopContent(
            modifier = Modifier.padding(
                top = Dimens.huge.dp
            )
        ) {
            EnjoyYourRide()
        }
        CenterContent {
            RideOngoingAnimation()
        }
        BottomContent {
          FinishRideButton(
              modifier = Modifier.padding(
                  bottom = Dimens.small.dp
              ), onClick = {

          })
        }
    }

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
private fun BottomContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
){
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
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
private fun EnjoyYourRide(
    modifier :Modifier  = Modifier
){
    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.large.dp),
        text = "Enjoy the ride with our",
        fontSize = Dimens.large.sp,
        color = Color.Black,
        textAlign = TextAlign.Center
    )

}

@Composable
private fun FinishRideButton(
    modifier: Modifier = Modifier,
    onClick:()->Unit
){
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ){
        Text(
            text = stringResource(R.string.screen_ride_finish_ride)
        )
    }

}