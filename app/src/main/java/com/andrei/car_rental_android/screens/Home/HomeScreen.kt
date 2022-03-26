package com.andrei.car_rental_android.screens.Home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationRequest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.ui.Dimens
import com.andrei.car_rental_android.utils.hasPermission
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker

sealed class PermissionState{
    object Unchecked:PermissionState()
    object Denied:PermissionState()
    object Granted:PermissionState()
}
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

    var currentPreviewedCar: Car? by remember {
        mutableStateOf(null)
    }

    LocationPermission(setKnowLocation = {
        viewModel.setLocation(it)
    }, setUnknownLocation = {
        viewModel.setLocationUnknown()
    })

    BottomSheet(car = currentPreviewedCar) {
        Box(modifier = Modifier.fillMaxSize()) {
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
            Column(modifier = Modifier.fillMaxSize().padding(bottom = Dimens.huge.dp), verticalArrangement = Arrangement.Bottom) {
                LocationState(locationState = viewModel.locationState.collectAsState().value)
            }
        }
    }
}


@Composable
private fun LocationPermission(
     setKnowLocation:(location:Location)-> Unit,
     setUnknownLocation:()->Unit
){
    var locationPermission:PermissionState by remember {
        mutableStateOf(PermissionState.Unchecked)
    }


    LocationSettings{ permissionGranted->
        if(permissionGranted){
            locationPermission = PermissionState.Granted
        }else{
            locationPermission = PermissionState.Denied
        }
    }


    when(locationPermission){
        PermissionState.Denied -> {
            Snackbar {
                Text(text = "Give permission for location")
            }
        }
        PermissionState.Granted -> {
           getLastKnownLocation(LocalContext.current, onNewLocation = {
                setKnowLocation(it)
           }, onError = {
               setUnknownLocation()
           })
        }
        PermissionState.Unchecked -> {
            //no nothing or maybe show some loading spinner
        }
    }
}


@Composable
private fun LocationState(
    locationState: HomeViewModel.LocationState
){
    when(locationState){
        is HomeViewModel.LocationState.Determined -> {
            Snackbar {
                Text(text = "We got you")
            }
        }
        is  HomeViewModel.LocationState.Loading -> {
            Snackbar {
              Text(text = "We are working hard to get your location")
            }
        }
        is HomeViewModel.LocationState.Unknown -> {
            Snackbar {
                Text(text = "We could not determine your location ")
            }
        }
    }
}


@SuppressLint("MissingPermission")
private fun getLastKnownLocation(
    context:Context,
    onNewLocation:(Location) -> Unit,
    onError:()->Unit
){
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val cancellationTokenSource = CancellationTokenSource()
    fusedLocationClient.getCurrentLocation(
        LocationRequest.QUALITY_HIGH_ACCURACY,
        cancellationTokenSource.token
    ).addOnSuccessListener { location: Location? ->
        if(location != null){
            onNewLocation(location)
        }else{
            onError()
        }
    }.addOnFailureListener {
         onError()
    }

}


@Composable
private fun LocationSettings(
    onPermissionResult:(granted:Boolean)->Unit
){
    val locationPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult ={locationEnabled->
             onPermissionResult(locationEnabled)
    } )

   val context = LocalContext.current

    LaunchedEffect(key1 = null){
        if(context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)){
            onPermissionResult(true)
        }else{
           //does not have permission but try to obtain it 
            locationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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
        }, sheetPeekHeight = 10.dp
    ){
       content()
    }

}