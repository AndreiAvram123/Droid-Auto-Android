package com.andrei.car_rental_android.screens.ride

import android.text.format.DateUtils
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.OngoingRide
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.composables.LoadingAlert
import com.andrei.car_rental_android.ui.Dimens


@Composable
fun RideScreen(
){
    MainContent()
}
@Composable
private fun MainContent() {
    val viewModel:RideViewModel = hiltViewModel<RideViewModelImpl>()
    viewModel.getOngoingRide()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.small.dp)
    ) {
        ScreenState(
            state = viewModel.currentRideState.collectAsState(),
            success = { ride ->
                  SuccessState(
                      ride = ride,
                      elapsedTime = viewModel.elapsedSeconds.collectAsState(),
                      totalCost = viewModel.rideCost.collectAsState()
                  )
            },
            loading = {
                LoadingAlert()
            },
            errorContent = {

            }
        )
    }

}


@Composable
private fun SuccessState(
    ride:OngoingRide,
    elapsedTime: State<Long>,
    totalCost:State<Double>
){
    TopContent(
        modifier = Modifier.padding(
            top = Dimens.huge.dp
        )
    ) {
        EnjoyYourRide(
            car = ride.car
        )
    }
    CenterContent {
        RideOngoingAnimation()
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Dimens.medium.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ElapsedTime(
                elapsedSeconds = elapsedTime
            )
            TotalCost(
                rideCost = totalCost
            )
        }
    }
    BottomContent {
        FinishRideButton(
            modifier = Modifier.padding(
                bottom = Dimens.small.dp
            ), onClick = {

            })
    }

}

@Composable
private fun ScreenState(
    state: State<RideViewModel.RideState>,
    success: @Composable (ride:OngoingRide)->Unit,
    loading:@Composable ()->Unit,
    errorContent:@Composable (errorMessage:String) -> Unit
){
    when(val stateValue = state.value){
        is RideViewModel.RideState.Success -> {
            success(stateValue.ongoingRide)
        }
        is RideViewModel.RideState.Loading -> {
            loading()
        }
        is RideViewModel.RideState.Error -> {
            errorContent(stateValue.message)
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
private fun TotalCost(
    modifier: Modifier = Modifier,
    rideCost:State<Double>
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(
            text = "Total cost",
            color = Color.Black,
            fontSize = Dimens.large.sp
        )
        Text(
            text =  "£ %.2f".format(rideCost.value),
            fontSize = Dimens.large.sp,
            color = Color.Black
        )
    }
}

@Composable
private fun ElapsedTime(
    modifier: Modifier = Modifier,
    elapsedSeconds:State<Long>
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(
            text = "Elapsed Time",
            color = Color.Black,
            fontSize = Dimens.large.sp
        )
        Text(
            text = DateUtils.formatElapsedTime(elapsedSeconds.value),
            fontSize = Dimens.large.sp,
            color = Color.Black
        )
    }
}




@Composable
private fun EnjoyYourRide(
    modifier :Modifier  = Modifier,
    car: Car
){
    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.large.dp),
        text = "Enjoy the ride with our ${car.model.manufacturerName} ${car.model.name}",
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