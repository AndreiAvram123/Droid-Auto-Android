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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.ui.Dimens
import com.andrei.car_rental_android.ui.composables.bitmapDescriptorFromVector
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

    val currentSelectedCarState: MutableState<Car?> =  remember {
        mutableStateOf(null)
    }

    LocationPermission(setKnowLocation = {
        viewModel.setLocation(it)
    }, setUnknownLocation = {
        viewModel.setLocationUnknown()
    })

    BottomSheet(
        carState = currentSelectedCarState,
        reserveCar = {
           viewModel.reserveCar(it)
        },
        reservationState = viewModel.reservationState.collectAsState(),
        reservationTimeLeft = viewModel.reservationTimeLeftMillis.collectAsState(),
        cancelReservation = {
            viewModel.cancelReservation()
        }

    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                )
            ) {
                MapContent(
                    state = viewModel.nearbyCars.collectAsState()
                ){
                    currentSelectedCarState.value= it
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = Dimens.huge.dp), verticalArrangement = Arrangement.Bottom) {
                LocationState(locationState = viewModel.locationState.collectAsState().value)
            }
        }
    }
}

@Composable
private fun MapContent(
    state : State<HomeViewModel.HomeViewModelState>,
    onMarkerClicked: (car: Car) -> Unit
){

    when(val stateValue = state.value){
        is HomeViewModel.HomeViewModelState.Success -> {
            MapMarkers(
                stateValue.data,
                onMarkerClicked = onMarkerClicked
            )
        }
        is HomeViewModel.HomeViewModelState.Loading -> {
        }
        else -> {

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
        onResult ={ locationEnabled->
            onPermissionResult(locationEnabled)
        }
    )

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
            },

            icon =  bitmapDescriptorFromVector(
                context = LocalContext.current,
                vectorResId = R.drawable.ic_car
            )
        )
    }
}


@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun BottomSheet(
    carState: State<Car?>,
    reservationState: State<HomeViewModel.ReservationState>,
    reserveCar:(car:Car)->Unit,
    reservationTimeLeft: State<Long>,
    cancelReservation: () -> Unit,
    content: @Composable ()->Unit
){

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    LaunchedEffect(carState.value){
        if(carState.value != null) {
            bottomSheetScaffoldState.bottomSheetState.expand()
        }
    }
    BottomSheetScaffold(
        modifier = Modifier.padding(bottom = Dimens.medium.dp),
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            BottomSheetContent(
                carState = carState,
                reservationState = reservationState,
                reserveCar = reserveCar,
                reservationTimeLeft = reservationTimeLeft,
                cancelReservation = cancelReservation

            )
        }, sheetPeekHeight = 15.dp
    ){
        content()
    }
}
@Composable
private fun BottomSheetContent(
    carState:State<Car?>,
    reservationState: State<HomeViewModel.ReservationState>,
    reservationTimeLeft:State<Long>,
    reserveCar:(car:Car)->Unit,
    cancelReservation: () -> Unit
) {

    BottomSheetLayout {
        val car = carState.value
        if (car != null) {
            CarDetails(
                modifier = Modifier.padding(
                    horizontal = Dimens.medium.dp
                ),
                car = car
            )
            ReservationState(
                reservationTimeLeft = reservationTimeLeft,
                reservationState = reservationState,
                car = car,
                reserveCar = reserveCar,
                cancelReservation = cancelReservation
            )
        }else{
            NoCarSelected(
                modifier = Modifier.padding(
                    horizontal = Dimens.medium.dp
                )
            )
        }


    }
}


@Composable
private fun ReservationState(
    reservationTimeLeft: State<Long>,
    reservationState: State<HomeViewModel.ReservationState>,
    car:Car,
    reserveCar:(car:Car)->Unit,
    cancelReservation: () -> Unit
){
    when (reservationState.value) {
        is HomeViewModel.ReservationState.Default -> {
            //no action
            ReserveButton(
                modifier = Modifier
                    .padding(Dimens.medium.dp)
            ) {
                reserveCar(car)
            }
        }
        is HomeViewModel.ReservationState.InProgress -> {
            LinearProgressIndicator(
                modifier = Modifier.padding(
                    horizontal = Dimens.medium.dp
                )
            )
        }
        is HomeViewModel.ReservationState.Reserved -> {
            ReservationTimeLeft(
                timeLeft = reservationTimeLeft
            )
            CancelReservationButton(
                modifier = Modifier.padding(
                    horizontal = Dimens.medium.dp
                )
            ){
                cancelReservation()
            }


        }
        is HomeViewModel.ReservationState.Error -> {

        }
    }
}

@Composable
private fun CancelReservationButton(
    modifier:Modifier = Modifier,
    cancelReservation:()->Unit

){
   Button(
       modifier = modifier.fillMaxWidth(),
       onClick = {
          cancelReservation()
       }) {
      Text(
          text = stringResource(R.string.screen_home_cancel_reservation)
      )
   }
}

@Composable
private fun ReservationTimeLeft(
    modifier:Modifier = Modifier,
    timeLeft:State<Long>
){
     Row(
         modifier = modifier.fillMaxWidth(),
         horizontalArrangement = Arrangement.Center
     ) {
         val timeSeconds = timeLeft.value
         val formattedSeconds:String = if(timeSeconds % 60 < 10){
             //add a zero to the beginning
             "0${timeSeconds % 60}"
         }else{
             (timeSeconds % 60).toString()
         }
        Text(
            text = "${timeSeconds /  60}:$formattedSeconds",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = Dimens.large.sp
        )
     }
}



@Composable
private fun BottomSheetLayout(
    content:@Composable ()->Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

@Composable
private fun NoCarSelected(
    modifier: Modifier
){
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Click on a car on the map to see the details here")
    }
}


@Composable
private fun ReserveButton(
    modifier:Modifier = Modifier,
    onClick:()->Unit
){
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Text(text = stringResource(R.string.screen_home_reserve))
    }
}
@Composable
private fun CarDetails(
    modifier:Modifier = Modifier,
    car:Car
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .size(100.dp),
        horizontalArrangement = Arrangement.Start
    ) {

        AsyncImage(
            modifier = Modifier.size(100.dp),
            model = car.model.image.url,
            contentDescription =null
        )
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = car.model.name,
                color = Color.Black
            )
            Spacer(
                modifier =  Modifier.width(Dimens.medium.dp)
            )
            Text(
                text = car.model.manufacturerName,
                color = Color.Black
            )
        }
    }
}

