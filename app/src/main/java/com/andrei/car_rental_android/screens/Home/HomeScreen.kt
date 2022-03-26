package com.andrei.car_rental_android.screens.Home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.andrei.car_rental_android.DTOs.Car
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker

@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        MainContent()
    }
}
@Composable
@Preview
fun MainContent() {
    val viewModel = hiltViewModel<HomeViewModelImpl>()
    viewModel.getNearbyCars()

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true
        )
    ) {
        Log.d("pupu","pupu")
        when(val nearbyCarsState = viewModel.nearbyCars.collectAsState().value) {
            is HomeViewModel.HomeViewModelState.Success -> {
               MapMarkers(nearbyCarsState.data)
            }
            is HomeViewModel.HomeViewModelState.Loading -> {
            }
            else -> {

            }
        }
    }


}

@Composable
private fun MapMarkers(nearbyCars: List<Car> ){
    nearbyCars.forEach {car ->
        Marker(position = car.location )
    }
}