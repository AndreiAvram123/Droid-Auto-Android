package com.andrei.car_rental_android.screens.receipt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.CarModel
import com.andrei.car_rental_android.DTOs.FinishedRide
import com.andrei.car_rental_android.DTOs.Image
import com.andrei.car_rental_android.ui.Dimens
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

@Composable
fun FinishedRideScreen(
    navController: NavController
){
    val viewModel = hiltViewModel<FinishedRideViewModelImpl>()
   MainContent(viewModel = viewModel )

}

@Composable
private fun MainContent(
    viewModel: FinishedRideViewModel
){
    LaunchedEffect(key1 = null ){
        viewModel.getReceipt()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.small.dp)
    ) {
       ScreenState(
           finishedRideState = viewModel.rideState.collectAsState()
       )
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
    val finishedRide = FinishedRide(
        id = 0,
        startTime = 1649711881,
        endTime = 1649794681,
        totalCharge = 2340,
        car = Car(
            model = CarModel(
                id = 0,
                name = "Aventator",
                manufacturerName = "Lamborghini",
                image = Image(
                    url = "https://robohash.org/40.77.167.63.png"
                )
            ),
            pricePerMinute = 14.3,
        )
    )
    SuccessContent(finishedRide = finishedRide)
}

@Composable
private fun SuccessContent(finishedRide: FinishedRide){
    Column {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TotalCharge(chargePence = finishedRide.totalCharge)
            CarImage(
                modifier = Modifier.padding(
                    top = Dimens.medium.dp
                ),
                image = finishedRide.car.model.image
            )
            CarModelAndMake(
                modifier = Modifier.padding(
                    top = Dimens.small.dp
                ),
                carModel = finishedRide.car.model
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = Dimens.large.dp,
                    start = Dimens.medium.dp,
                    end = Dimens.medium.dp
                )
        ) {
            RideStartDate(startTime = finishedRide.startTime)
            Spacer(modifier = Modifier.height(Dimens.large.dp))
            RideEndDate(endTime = finishedRide.endTime)
        }
    }
}


@Composable
private fun TotalCharge(
    modifier: Modifier = Modifier,
    chargePence:Long
){
    Text(
        modifier = modifier
            .padding(
                top = Dimens.huge.dp
            ),
        text = "£%.2f".format((chargePence/100.0)),
        color = Color.Black,
        fontWeight = FontWeight.SemiBold,
        fontSize = Dimens.huge.sp
    )
}
@Composable
private fun CarImage(
    modifier: Modifier = Modifier,
    image: Image
){
    AsyncImage(
        modifier = modifier
            .size(200.dp)
            .clip(RoundedCornerShape(Dimens.medium.dp)),
        model = image.url,
        contentDescription =null
    )
}

@Composable
private fun CarModelAndMake(
    modifier: Modifier = Modifier,
    carModel: CarModel)
{
    Text(
        modifier = modifier,
        text = "${carModel.manufacturerName} ${carModel.name}",
        fontSize = Dimens.medium.sp,
    )
}

@Composable
private fun RideStartDate(
    startTime:Long
){
    InformationRow {
        StartLabel(text = "Started")
        FieldValue(text = formatRideDate(timeSeconds = startTime))
    }
}


@Composable
private fun StartLabel(
    text:String
){
    Text(
        text = text,
        color = Color.Gray,
        fontSize = Dimens.large.sp
    )
}


@Composable
private fun InformationRow(
    content: @Composable ()->Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        content()
    }
}


@Composable
private fun RideEndDate(
    endTime:Long
){
  InformationRow{
       StartLabel(text = "Finished")
        FieldValue(
            text = formatRideDate(
                timeSeconds = endTime
            )
        )
    }
}

@Composable
private fun FieldValue(
    text:String
){
    Text(
        text= text,
        fontSize = Dimens.big.sp
    )
}

@Composable
private fun formatRideDate(
    timeSeconds:Long
):String{
    val date = Date(timeSeconds * 1000)
    val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

    val pattern =  if(localDate.year == Calendar.YEAR){
        "dd MMMM, HH:mm "
    }else{
        "dd MMMM YYYY, HH:mm "
    }

    val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
    return sdf.format(date)

}

