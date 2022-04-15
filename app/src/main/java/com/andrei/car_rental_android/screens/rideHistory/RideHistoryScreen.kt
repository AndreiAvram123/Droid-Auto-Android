package com.andrei.car_rental_android.screens.rideHistory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.andrei.car_rental_android.DTOs.FinishedRide

@Composable
fun RideHistoryScreen(){
    val viewModel = hiltViewModel<RideHistoryViewModelImpl>()
   MainContent(
       viewModel = viewModel
   )
}

@Composable
private fun MainContent(
    viewModel: RideHistoryViewModel
){
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
         ScreenState(
             viewModel.screenState.collectAsState()
         )
    }
}

@Composable
private fun ScreenState(
    screenStateCompose: State<RideHistoryViewModel.ScreenState>
){
    when(val screenState = screenStateCompose.value){
        is RideHistoryViewModel.ScreenState.Success->{
            RideList(rideHistory = screenState.data)
        }
        is  RideHistoryViewModel.ScreenState.Loading -> {
        }
        is RideHistoryViewModel.ScreenState.Error -> {

        }
    }
}

@Composable
private fun RideList(
    rideHistory:List<FinishedRide>
){
    LazyColumn{
        items(rideHistory){ ride->
            Text(
                text = ride.id.toString()
            )
        }
    }
}