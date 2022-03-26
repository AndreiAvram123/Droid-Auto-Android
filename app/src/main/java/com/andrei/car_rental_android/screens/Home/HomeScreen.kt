package com.andrei.car_rental_android.screens.Home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
@OptIn(ExperimentalMaterialApi::class)
fun MainContent() {
    val viewModel = hiltViewModel<HomeViewModelImpl>()
    viewModel.getNearbyCars()
    var currentPreviewedCar: Car? by remember {
        mutableStateOf(null)
    }
    BottomSheet(car = currentPreviewedCar) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
            )
        ) {

            when (val nearbyCarsState = viewModel.nearbyCars.collectAsState().value) {
                is HomeViewModel.HomeViewModelState.Success -> {
                    MapMarkers(
                        nearbyCarsState.data,
                        onMarkerClicked = {
                            currentPreviewedCar = it
                        }
                    )
                }
                is HomeViewModel.HomeViewModelState.Loading -> {
                }
                else -> {

                }
            }
        }

    }
}

@Composable
private fun MapMarkers(
    nearbyCars: List<Car>,
    onMarkerClicked:(car:Car)-> Unit
){
    nearbyCars.forEach {car ->
        Marker(
            position = car.location,
            onClick = {
                onMarkerClicked(car)
                true
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun BottomSheet(
     car: Car?,
     content: @Composable ()->Unit
){

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    LaunchedEffect(car){
        if(car != null) {
            bottomSheetScaffoldState.bottomSheetState.expand()
        }
    }
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
               Column(
                   modifier = Modifier.fillMaxSize(),
                   verticalArrangement = Arrangement.Center
               ) {
                   Row(
                       modifier = Modifier.fillMaxWidth(),
                       horizontalArrangement = Arrangement.Center
                   ) {
                       if(car != null){
                           Text(
                               text = car.model.name,
                               color = Color.Black
                           )
                           Text(
                               text = car.model.manufacturerName,
                               color = Color.Black
                           )
                       }else{
                           Text(text = "Click on a car on the map to see the details here")
                       }
                   }
               }
            }
        }, sheetPeekHeight = 20.dp
    ){
        content()
    }

}