package com.andrei.car_rental_android.screens.finishedRide

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Paid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.FinishedRide
import com.andrei.car_rental_android.DTOs.Image
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.engine.utils.TestData
import com.andrei.car_rental_android.screens.register.base.BackButton
import com.andrei.car_rental_android.ui.Dimens
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun FinishedRideScreen(
    navController: NavController
){


    val viewModel = hiltViewModel<FinishedRideViewModelImpl>()
    MainContent(
        viewModel = viewModel,
        navController = navController
    )

}

@Composable
private fun MainContent(
    viewModel: FinishedRideViewModel,
    navController: NavController
){
    LaunchedEffect(null ){
        viewModel.getReceipt()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column{
            BackButton{
                navController.popBackStack()
            }
            ScreenState(
                finishedRideState = viewModel.rideState.collectAsState()
            )
        }

    }
}

@Composable
private fun ScreenState(
    finishedRideState: State<FinishedRideViewModel.ScreenState>
){
    when(val state = finishedRideState.value){
        is FinishedRideViewModel.ScreenState.Success -> {
            SuccessContent(finishedRide = state.finishedRide)

        }
        is FinishedRideViewModel.ScreenState.Error -> {

        }
        is FinishedRideViewModel.ScreenState.Loading -> {

        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun PreviewSuccessContent(){
    SuccessContent(finishedRide = TestData.finishedRide)
}

@Composable
private fun SuccessContent(finishedRide: FinishedRide){
    Column(
        modifier = Modifier
            .padding(
                horizontal = Dimens.huge.dp
            )
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.huge.dp)
    ){

        CarDetails(
            modifier = Modifier.padding(
                top = Dimens.small.dp
            ),
            car = finishedRide.car
        )
        TotalCharge(
            chargePence = finishedRide.totalCharge
        )
        RideStartDate(
            startTime = finishedRide.startTime
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
                    horizontal = Dimens.huge.dp,
                    vertical = 40.dp
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
private fun TotalCharge(
    chargePence:Long
){
    InformationBox {
        StartLabel(
            text = stringResource(R.string.screen_finished_ride_total_charge),
            imageVector = Icons.Filled.Paid
        )
        FieldValue(
            text = "£%.2f".format((chargePence / 100.0))
        )
    }
}

@Composable
private fun CarImage(
    modifier: Modifier = Modifier,
    image: Image
){

    AsyncImage(
        modifier = modifier
            .height(150.dp)
            .clip(RoundedCornerShape(Dimens.large.dp)),
        model = image.url,
        placeholder = painterResource(R.drawable.car_placeholder),
        contentDescription =null
    )
}

@Composable
private fun CarDetails(
    modifier: Modifier = Modifier,
    car: Car
) {

    Text(
        modifier = modifier,
        text = "${car.model.manufacturerName} ${car.model.name}",
        fontSize = Dimens.large.sp,
        fontWeight = FontWeight.SemiBold
    )
    CarImage(
        image = car.model.image
    )
}

@Composable
private fun RideStartDate(
    startTime:LocalDateTime
){
    InformationBox {
        StartLabel(
            text = stringResource(R.string.screen_finished_ride_started),
            imageVector = Icons.Filled.Event
        )
        FieldValue(text = formatRideDate(
            localDateTime = startTime))
    }
}


@Composable
private fun StartLabel(
    text:String,
    imageVector:ImageVector
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

private fun formatRideDate(
    localDateTime: LocalDateTime
):String{


    val pattern =  if(localDateTime.year == Calendar.YEAR){
        "dd MMMM, HH:mm "
    }else{
        "dd MMMM YYYY, HH:mm "
    }

    val sdf = DateTimeFormatter.ofPattern(pattern)
    return sdf.format(localDateTime)

}

