package com.andrei.car_rental_android.screens.Home

import android.Manifest
import android.app.Activity
import android.location.Location
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.PaymentResponse
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.helpers.LocationHelper
import com.andrei.car_rental_android.helpers.LocationHelperImpl
import com.andrei.car_rental_android.screens.Home.HomeViewModel.CarReservationState
import com.andrei.car_rental_android.ui.Dimens
import com.andrei.car_rental_android.ui.composables.bitmapDescriptorFromVector
import com.andrei.car_rental_android.utils.hasPermission
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetContract
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.flow.filterNotNull


@Composable
fun HomeScreen(
    navController: NavController,
) {
    MainContent(
        navController
    )
}
@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun MainContent(
    navController: NavController
) {
  val context = LocalContext.current
    val locationHelper = LocationHelperImpl(context)
    val viewModel = hiltViewModel<HomeViewModelImpl>()


    val paymentSheetLauncher = rememberLauncherForActivityResult(
        contract = PaymentSheetContract(),
        onResult = { paymentResult ->
            when (paymentResult) {
                is PaymentSheetResult.Canceled -> {

                }
                is PaymentSheetResult.Failed -> {

                }
                is PaymentSheetResult.Completed -> {

                }
            }
        }
    )

    DisposableEffect(key1 = context){
        onDispose {
            locationHelper.stopLocationUpdates()
        }
    }

    LaunchedEffect(key1 = viewModel){
        locationHelper.lastKnownLocation.filterNotNull().collect{
            viewModel.setLocationState(HomeViewModel.LocationState.Resolved(it))
        }

    }

    val currentSelectedCarState: MutableState<Car?> =  remember {
        mutableStateOf(null)
    }

    LocationRequirements(
        locationHelper,
        viewModel.locationRequirements.collectAsState(),
        onRequirementMet = { requirementResolved->
            viewModel.setLocationRequirementResolved(requirementResolved)
        }, onRequirementFailed = {
            //todo
        }, onAllRequirementsResolved = {
            viewModel.setLocationState(HomeViewModel.LocationState.Loading)
            locationHelper.getLastKnownLocation(onLocationResolved ={
                locationHelper.requestLocationUpdates(
                    locationRequest = locationHelper.balancedPrecisionHighIntervalRequest
                )
            }, onError = {
                viewModel.setLocationState(HomeViewModel.LocationState.Unknown)
            } )
        })


    BottomSheet(
        carState = currentSelectedCarState,
        carReservationState = viewModel.carReservationState.collectAsState(),
        reservationTimeLeft = viewModel.reservationTimeLeftMillis.collectAsState(),
        reservationStateListener = object : ReservationStateListener {
            override fun reserveCar(car: Car) = viewModel.reserveCar(car)
            override fun cancelReservation()  = viewModel.cancelReservation()
            override fun payUnlockFee() = viewModel.startUnlockPaymentProcess()
            override fun onUnlockPaymentIntentReady(paymentResponse: PaymentResponse) {
                PaymentConfiguration.init(context,paymentResponse.publishableKey)
                val googlePayConfiguration = PaymentSheet.GooglePayConfiguration(
                    environment = PaymentSheet.GooglePayConfiguration.Environment.Test,
                    countryCode = "UK"
                )
                val configuration = PaymentSheet.Configuration.Builder("car-rental")
                    .googlePay(googlePayConfiguration)
                    .build()
                paymentSheetLauncher.launch(PaymentSheetContract.Args.createPaymentIntentArgs(
                    clientSecret = paymentResponse.clientSecret,
                    config = configuration
                ))
            }
        }
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Map(
                currentLocation = locationHelper.lastKnownLocation.collectAsState(),
                onCarSelected = {
                    currentSelectedCarState.value = it
                },
                state = viewModel.nearbyCars.collectAsState(),
            )
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
private fun RequestActiveLocation(
    locationHelper: LocationHelper,
    onLocationEnabled : () -> Unit,
){
    val showEnableLocationSnackbar =  remember{
        mutableStateOf(false)
    }
    EnableLocationSnackbar(showEnableLocationSnackbar = showEnableLocationSnackbar)

    val locationSettingsLauncher =  rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()){
        when (it.resultCode) {
            Activity.RESULT_OK -> {
                onLocationEnabled()
                showEnableLocationSnackbar.value = false
            }
            Activity.RESULT_CANCELED -> showEnableLocationSnackbar.value = true
        }

    }
    locationHelper.checkLocationSettings(locationSettingsLauncher){
        onLocationEnabled()
    }
}

@Composable
private fun LocationRequirements(
    locationHelper: LocationHelper,
    locationRequirements:State<Set<HomeViewModel.LocationRequirement>>,
    onRequirementMet:(locationRequirement:HomeViewModel.LocationRequirement) -> Unit,
    onRequirementFailed:(locationRequirement:HomeViewModel.LocationRequirement)->Unit,
    onAllRequirementsResolved:()->Unit
){
    val requirement = locationRequirements.value.firstOrNull()


    if(requirement == null){
        onAllRequirementsResolved()
        return;
    }
    when(requirement){
        HomeViewModel.LocationRequirement.PermissionNeeded -> {
            LocationPermission(onPermissionGranted = {
                onRequirementMet(HomeViewModel.LocationRequirement.PermissionNeeded)
            },onPermissionDenied = {
                onRequirementFailed(HomeViewModel.LocationRequirement.PermissionNeeded)
            })


        }
        HomeViewModel.LocationRequirement.LocationActive -> {
            RequestActiveLocation(
                locationHelper,
                onLocationEnabled = {
                    onRequirementMet(HomeViewModel.LocationRequirement.LocationActive)
                }
            )
        }

    }
}


@Composable
private fun EnableLocationSnackbar(
    showEnableLocationSnackbar: State<Boolean>
){
    if(showEnableLocationSnackbar.value){
        Snackbar {
            Text(text = "Please enable location");
        } }
}

@Composable
private fun Map(
    currentLocation:State<Location?>,
    state: State<HomeViewModel.HomeViewModelState>,
    onCarSelected:(car:Car)->Unit,
){
    val cameraPositionState = rememberCameraPositionState()

    val currentLocationValue = currentLocation.value
    if(currentLocationValue != null){
        cameraPositionState.position = CameraPosition.fromLatLngZoom(
            LatLng(
                currentLocationValue.latitude,
                currentLocationValue.longitude
            ),15f
        )
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
        )
    ) {
        MapContent(
            state = state
        ){
            onCarSelected(it)
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
    onPermissionGranted: ()->Unit,
    onPermissionDenied: ()->Unit

){

    LocationSettings{ permissionGranted->
        if(permissionGranted){
            onPermissionGranted()
        }else{
            onPermissionDenied()
        }
    }

}




@Composable
private fun LocationState(
    locationState: HomeViewModel.LocationState
){
    when(locationState){
        is HomeViewModel.LocationState.Resolved -> {
            //no action required here
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
        HomeViewModel.LocationState.NotRequested -> {

        }
    }
}



@Composable
private fun LocationSettings(
    onPermissionResult:(granted:Boolean)->Unit
){

    //maybe move to a helper
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
    carReservationState: State<CarReservationState>,
    reservationTimeLeft: State<Long>,
    reservationStateListener: ReservationStateListener,
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
            BottomSheetLayout {
                BottomSheetContent(
                    carState = carState,
                    carReservationState = carReservationState,
                    reservationTimeLeft = reservationTimeLeft,
                    reservationStateListener = reservationStateListener

                )
            }
        }, sheetPeekHeight = 15.dp
    ){
        content()
    }
}




@Composable
private fun BottomSheetContent(
    carState:State<Car?>,
    carReservationState: State<CarReservationState>,
    reservationTimeLeft:State<Long>,
    reservationStateListener: ReservationStateListener

) {

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
            carReservationState = carReservationState,
            currentPreviewedCar = car,
            reservationStateListener = reservationStateListener
        )


    }else{
        NoCarSelected(
            modifier = Modifier.padding(
                horizontal = Dimens.medium.dp
            )
        )
    }

}



@Composable
private fun ReservationState(
    reservationTimeLeft : State<Long>,
    carReservationState:State<CarReservationState>,
    currentPreviewedCar: Car,
    reservationStateListener: ReservationStateListener
){

    when (val stateValue = carReservationState.value) {
        is CarReservationState.Default -> {
            //no action
            ReserveButton(
                modifier = Modifier
                    .padding(Dimens.medium.dp)
            ) {
               reservationStateListener.reserveCar(currentPreviewedCar)
            }
        }
        is CarReservationState.InProgress -> {
            LinearProgressIndicator(
                modifier = Modifier.padding(
                    horizontal = Dimens.medium.dp
                )
            )
        }
        is CarReservationState.Reserved -> {
            ReservationTimeLeft(
                timeLeft = reservationTimeLeft
            )
            CancelReservationButton(
                modifier = Modifier.padding(
                    horizontal = Dimens.medium.dp
                ), cancelReservation = { reservationStateListener.cancelReservation() })


        }
        is CarReservationState.Error -> {

        }
        is CarReservationState.PaymentState.ReadyForUnlockPayment-> {
            UnlockFeeHint(
                modifier = Modifier.padding(
                    horizontal = Dimens.medium.dp,
                    vertical = Dimens.medium.dp
                )
            )
            PaymentButton(
                modifier = Modifier.padding(
                    horizontal = Dimens.medium.dp
                ), payUnlockFee = { reservationStateListener.payUnlockFee() })
        }
        is CarReservationState.PaymentState.LoadingPaymentData -> {
            LinearProgressIndicator(
                modifier = Modifier.padding(
                    horizontal = Dimens.medium.dp
                )
            )
        }
        is CarReservationState.PaymentState.PaymentDataReady ->{
            reservationStateListener.onUnlockPaymentIntentReady(stateValue.paymentResponse)
        }
    }
}


@Composable
private fun UnlockFeeHint(
    modifier:Modifier = Modifier
){
        Text(
            modifier = modifier.fillMaxWidth(),
            color = Color.Black,
            textAlign = TextAlign.Center,
            text = stringResource(R.string.screen_home_unlock_fee)
        )
}
@Composable
private fun PaymentButton(
    modifier:Modifier = Modifier,
    payUnlockFee:()->Unit
){
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = payUnlockFee
    ) {
        Text(text = stringResource(R.string.screen_home_pay))
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
        Column(modifier = Modifier.fillMaxSize()) {
            content()
        }

    }
}

@Composable
private fun NoCarSelected(
    modifier: Modifier
){
    Row(
        modifier = modifier.fillMaxSize(),
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
