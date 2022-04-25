package com.andrei.car_rental_android.screens.ride

import android.text.format.DateUtils
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.OngoingRide
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.composables.LoadingAlert
import com.andrei.car_rental_android.engine.utils.TestData
import com.andrei.car_rental_android.screens.register.base.CustomButton
import com.andrei.car_rental_android.screens.register.base.CustomOutlinedButton
import com.andrei.car_rental_android.ui.Dimens
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


@Composable
fun RideScreen(
    navController: NavController
){
    val viewModel:RideViewModel = hiltViewModel<RideViewModelImpl>()
    val navigator = RideNavigatorImpl(
        navController = navController
    )
    MainContent(
        viewModel = viewModel,
        navigator = navigator
    )
}
@Composable
private fun MainContent(
    viewModel: RideViewModel,
    navigator: RideNavigator
) {

    LaunchedEffect(null){
        viewModel.getOngoingRide()
    }

    NavigationState (
        rideState = viewModel.currentRideState.collectAsState() ,
        navigator = navigator
    )


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
                    elapsedTime = viewModel.elapsedTime.collectAsState(),
                    endRide = viewModel::finishRide,
                    carLocked = viewModel.carLocked.collectAsState(),
                    lockCar = viewModel::lockCar,
                    unlockCar = viewModel::unlockCar
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
private fun NavigationState(
    rideState: State<RideViewModel.RideState>,
    navigator: RideNavigator
){

    val showLoading  = remember {
        derivedStateOf {
            rideState.value is RideViewModel.RideState.FinishingRide
        }
    }
    if(showLoading.value){
        LoadingAlert(stringResource(id = R.string.screen_ride_finishing_ride))
    }
    LaunchedEffect(key1 = rideState.value){
        val stateValue = rideState.value
        if(stateValue is RideViewModel.RideState.RideFinished){
            navigator.navigateToReceiptScreen(stateValue.finishedRide)
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewSuccessState(){
    val ride = TestData.ongoingRide

    val elapsedTime:State<Duration> = remember {
        mutableStateOf(100.seconds)
    }
    val carLocked = remember {
        mutableStateOf(false)
    }

    SuccessState(
        ride = ride,
        elapsedTime = elapsedTime,
        endRide = {},
        lockCar = {},
        unlockCar = {},
        carLocked = carLocked
    )
}

@Composable
private fun SuccessState(
    ride:OngoingRide,
    elapsedTime: State<Duration>,
    endRide:()->Unit,
    carLocked:State<Boolean>,
    lockCar:()->Unit,
    unlockCar:()->Unit
){
    Column(
        modifier = Modifier.padding(
            vertical = Dimens.extraLarge.dp
        )
    ) {

        EnjoyYourRide(
            car = ride.car,
        )

        RideOngoingAnimation(
            modifier = Modifier.padding(
                vertical = Dimens.large.dp
            )
        )
        Column(
            modifier = Modifier.padding(
                horizontal = 40.dp
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.huge.dp)
        ) {
            ElapsedTime(
                elapsedSeconds = elapsedTime
            )
            TotalCost(
                elapsedTime = elapsedTime,
                pricePerMinute = ride.car.pricePerMinute
            )
            BottomContent {
                ActionButtons(
                    carLocked = carLocked,
                    endRide = endRide,
                    lockCar = lockCar,
                    unlockCar = unlockCar
                )
            }
        }
    }

}

@Composable
private fun ActionButtons(
    carLocked: State<Boolean>,
    lockCar: () -> Unit,
    unlockCar: () -> Unit,
    endRide: () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if(carLocked.value) {
            CustomOutlinedButton(
                text = stringResource(R.string.screen_ride_unlock),
                imageVector = Icons.Filled.LockOpen,
                onClick = unlockCar
            )
        }else{
            CustomOutlinedButton(
                text = stringResource(R.string.screen_ride_lock),
                imageVector = Icons.Filled.Lock,
                onClick = lockCar
            )
        }
        CustomButton(
            text = stringResource(R.string.screen_ride_end_ride),
            onClick = endRide
        )
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
private fun RideOngoingAnimation(
    modifier: Modifier = Modifier
){
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ride_ongoing))
    LottieAnimation(
        modifier = modifier,
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
}


@Composable
private fun TotalCost(
    elapsedTime: State<Duration>,
    pricePerMinute:Long
){
    val rideCost = elapsedTime.value.inWholeMinutes * (pricePerMinute/100.0)
    InformationBox {
        StartLabel(
            text = stringResource(R.string.screen_ride_total_cost),
            imageVector = Icons.Filled.Schedule
        )
        FieldValue(
            text ="£ %.2f".format(rideCost)
        )
    }
}

@Composable
private fun ElapsedTime(
    elapsedSeconds:State<Duration>
){
    InformationBox {
        StartLabel(
            text = stringResource(R.string.screen_ride_elapsed_seconds),
            imageVector = Icons.Filled.Schedule
        )
        FieldValue(
            text = DateUtils.formatElapsedTime(
                elapsedSeconds.value.inWholeSeconds
            )
        )
    }
}






@Composable
private fun InformationBox(
    content:@Composable ()->Unit
){
    Box(
        modifier = Modifier.border(
            width = 2.dp,
            color = Color.LightGray,
            shape = RoundedCornerShape(Dimens.big.dp)
        )
    ){
        Column(
            modifier = Modifier
                .padding(
                    Dimens.huge.dp
                )
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.large.dp)
        ) {
            content()
        }
    }
}


@Composable
private fun StartLabel(
    text:String,
    imageVector: ImageVector
){
    Row (
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = imageVector,
            contentDescription = null
        )
        Spacer(
            modifier = Modifier.width(Dimens.small.dp)
        )
        Text(
            text = text,
            color = Color.Gray,
            fontSize = Dimens.large.sp
        )
    }
}



@Composable
private fun FieldValue(
    text:String
){
    Text(
        text= text,
        fontSize = Dimens.large.sp,
        fontWeight = FontWeight.SemiBold
    )
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