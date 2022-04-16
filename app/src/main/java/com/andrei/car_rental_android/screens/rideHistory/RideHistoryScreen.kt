package com.andrei.car_rental_android.screens.rideHistory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.andrei.car_rental_android.DTOs.FinishedRide
import com.andrei.car_rental_android.DTOs.Image
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.engine.utils.TestData
import com.andrei.car_rental_android.screens.register.base.BackButton
import com.andrei.car_rental_android.ui.Dimens
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun RideHistoryScreen(
    navController: NavController
){
    val viewModel = hiltViewModel<RideHistoryViewModelImpl>()
    val navigator = RideHistoryNavigatorImpl(
        navController = navController
    )
   MainContent(
       viewModel = viewModel,
       navigator = navigator
   )
}

@Composable
private fun MainContent(
    viewModel: RideHistoryViewModel,
    navigator: RideHistoryNavigator
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                colorResource(
                    R.color.dark_white
                )
            )
    ) {
        Box(
            modifier = Modifier.padding(
                vertical = Dimens.tiny.dp
            ), contentAlignment = Alignment.CenterStart
        ) {
            BackButton(
                navigateBack = navigator::navigateBack
            )
           Text(
               modifier = Modifier.fillMaxWidth(),
               textAlign = TextAlign.Center,
               fontSize = Dimens.large.sp,
               text = stringResource(R.string.screen_ride_history_title)
           )
        }
         ScreenState(
             viewModel.screenState.collectAsState(),
             navigateToFinishedRideScreen = {
                 navigator.navigateToFinishedRideScreen(it)
             }
         )
    }
}


@Composable
private fun ScreenState(
    screenStateCompose: State<RideHistoryViewModel.ScreenState>,
    navigateToFinishedRideScreen : (finishedRide:FinishedRide)->Unit
){
    when(val screenState = screenStateCompose.value){
        is RideHistoryViewModel.ScreenState.Success->{
            RideList(rideHistory = screenState.data, navigateToFinishedRideScreen = navigateToFinishedRideScreen)
        }
        is  RideHistoryViewModel.ScreenState.Loading -> {}
        is RideHistoryViewModel.ScreenState.Error -> {}
    }
}

@Composable
private fun RideList(
    rideHistory:List<FinishedRide>,
    navigateToFinishedRideScreen : (finishedRide:FinishedRide)->Unit
){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = Dimens.large.dp,
                start = Dimens.medium.dp,
                end = Dimens.medium.dp
            )
    ){
        items(rideHistory){ ride->
             RideItem(
                 finishedRide = ride ,
                 navigateToFinishedRideScreen ={
                     navigateToFinishedRideScreen(ride)
                 }
             )
        }
    }
}

@Composable
private fun RideItem(
    finishedRide: FinishedRide,
    navigateToFinishedRideScreen: () -> Unit
){
    RideRowLayout(modifier = Modifier.clickable {
        navigateToFinishedRideScreen()
    }) {
        RideRowContent(
            finishedRide = finishedRide
        )
    }
}


@Composable
private  fun RideRowLayout(
     modifier :Modifier,
     content: @Composable ()->Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = Dimens.small.dp
            )
            .height(120.dp),
        elevation = Dimens.tiny.dp,
        backgroundColor = Color.White
    ) {
        content()
    }
}


@Composable
private fun RideRowContent(
    modifier:Modifier = Modifier,
    finishedRide: FinishedRide
){
    Row(
        modifier = modifier
            .padding(
                Dimens.medium.dp,
            )
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CarImage(
            image = finishedRide.car.model.image
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            RideDate(
                dateTime = finishedRide.startTime
            )
            RideCost(
                amount = finishedRide.totalCharge
            )
        }

    }
}

@Composable
private fun CarImage(
     image: Image
){
    AsyncImage(
        modifier = Modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(Dimens.small.dp)),
        model = image.url,
        contentDescription = null
    )
}


@Preview
@Composable
private fun PreviewRide(){
   RideItem(finishedRide = TestData.finishedRide) {

   }
}


@Composable
private fun RideCost(
    amount:Long
){
    Text(
        text = "£%.2f".format(amount/100.0) ,
        color = Color.Black,
        fontWeight = FontWeight.SemiBold,
        fontSize = Dimens.medium.sp
    )
}

@Composable
private fun RideDate(dateTime:LocalDateTime){
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    val text  = dateFormatter.format(dateTime)
    Text(
        text = text ,
        color = Color.Black,
        fontWeight = FontWeight.SemiBold,
        fontSize = Dimens.medium.sp
    )
}