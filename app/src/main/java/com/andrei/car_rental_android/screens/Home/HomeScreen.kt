package com.andrei.car_rental_android.screens.Home

import android.Manifest
import android.app.Activity
import android.location.Location
import android.text.format.DateUtils
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.andrei.car_rental_android.DTOs.Car
import com.andrei.car_rental_android.DTOs.CarWithLocation
import com.andrei.car_rental_android.DTOs.PaymentResponse
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.composables.LoadingAlert
import com.andrei.car_rental_android.engine.response.DirectionStep
import com.andrei.car_rental_android.helpers.PaymentConfigurationHelper
import com.andrei.car_rental_android.screens.Home.states.DirectionsState
import com.andrei.car_rental_android.screens.Home.states.HomeViewModelState
import com.andrei.car_rental_android.screens.Home.states.SelectedCarState
import com.andrei.car_rental_android.screens.Home.states.UnlockPaymentState
import com.andrei.car_rental_android.ui.Dimens
import com.andrei.car_rental_android.ui.composables.LoadingSnackbar
import com.andrei.car_rental_android.ui.composables.bitmapDescriptorFromVector
import com.andrei.car_rental_android.utils.hasPermission
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheetContract
import kotlin.time.Duration


@Composable
fun HomeScreen(
    navController: NavController,
    openDrawer:()->Unit
) {
    val viewModel:HomeViewModel = hiltViewModel<HomeViewModelImpl>()
    val navigator = HomeNavigatorImpl(
        navController = navController
    )
    MainContent(
        openDrawer = openDrawer,
        homeNavigator = navigator,
        viewModel = viewModel
    )

}

@Composable
private fun MainContent(
    openDrawer: () -> Unit,
    viewModel: HomeViewModel,
    homeNavigator: HomeNavigator
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = null){
        viewModel.checkForReservationOrRide()
    }

    ScreenNavigation(
        homeNavigationState = viewModel.navigationState.collectAsState(
            initial = HomeNavigator.HomeNavigationState.Default
        ),
        homeNavigator = homeNavigator
    )

    val paymentSheetLauncher = rememberLauncherForActivityResult(
        contract = PaymentSheetContract(),
        onResult = viewModel::onFeePaymentResult
    )

    val currentCarSelected: MutableState<Car?> =  remember {
        mutableStateOf(null)
    }


    LocationRequirements(
        viewModel.locationRequirements.collectAsState(),
        onRequirementMet = { requirementResolved->
            viewModel.notifyRequirementResolved(requirementResolved)
        }, onRequirementFailed = { },
        checkLocationSettings = viewModel::checkLocationSettings
    )

    BottomSheet(
        carSelected = currentCarSelected,
        selectedCarState = viewModel.selectedCarState.collectAsState(),
        unlockPaymentState = viewModel.unlockPaymentState.collectAsState(),
        reservationTimeLeft = viewModel.reservationTimeLeft.collectAsState(),
        reservationStateListener = object : ReservationStateListener {
            override fun reserveCar(car: Car) = viewModel.reserveCar(car)
            override fun cancelReservation()  = viewModel.cancelReservation()
            override fun payUnlockFee() = viewModel.startUnlockPaymentProcess()
            override fun onPaymentDataReady(paymentResponse: PaymentResponse) {
                PaymentConfiguration.init(context,paymentResponse.publishableKey)
                val configuration = PaymentConfigurationHelper.buildConfiguration(
                    customerID = paymentResponse.customerID,
                    customerKey = paymentResponse.customerKey
                )
                paymentSheetLauncher.launch(PaymentSheetContract.Args.createPaymentIntentArgs(
                    clientSecret = paymentResponse.clientSecret,
                    config = configuration
                ))
            }
        }
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Map(
                cameraLocation = viewModel.cameraPosition.collectAsState(),
                onCarSelected = {
                    currentCarSelected.value = it
                },
                state = viewModel.nearbyCarsState.collectAsState(),
                directionsState = viewModel.directionsState.collectAsState(),
                reservedCarLocationState = viewModel.reservedCarLocation.collectAsState()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = Dimens.huge.dp), verticalArrangement = Arrangement.Bottom) {
            LocationState(
                locationState = viewModel.locationState.collectAsState()
            )
           }
            DrawerButton(
                modifier = Modifier.padding(
                    start = Dimens.medium.dp,
                    top = Dimens.medium.dp
                ), onClick = openDrawer
            )
        }
    }
}

@Composable
private fun DrawerButton(
    modifier:Modifier = Modifier,
    onClick: () -> Unit
){
    FloatingActionButton(
         modifier =  modifier.size(48.dp),
        onClick = onClick,
        backgroundColor = colorResource(R.color.dark_white)
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = Icons.Filled.Menu ,
            contentDescription = null
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewBottomSheetNoCar(){
    BottomSheetLayout {
        NoCarSelected()
    }
}


@Composable
private fun ScreenNavigation(
    homeNavigationState: State<HomeNavigator.HomeNavigationState>,
    homeNavigator: HomeNavigator
){

    LaunchedEffect(key1 = homeNavigationState.value ){
        if(homeNavigationState.value is HomeNavigator.HomeNavigationState.NavigateToRideScreen){
            homeNavigator.navigateToOngoingRide()
        }
    }
}



@Composable
private fun ActiveLocation(
    checkLocationSettings:(locationSettingsLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>, onLocationEnabled: () -> Unit)->Unit,
    onActiveLocationResult:(active:Boolean)->Unit
){

    val locationSettingsLauncher =  rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()){
        when (it.resultCode) {
            Activity.RESULT_OK -> onActiveLocationResult(true)
            Activity.RESULT_CANCELED -> onActiveLocationResult(false)
        }

    }
    checkLocationSettings(locationSettingsLauncher){
        onActiveLocationResult(true)
    }
}

@Composable
private fun LocationRequirements(
    locationRequirements:State<Set<HomeViewModel.LocationRequirement>>,
    onRequirementMet:(locationRequirement:HomeViewModel.LocationRequirement) -> Unit,
    onRequirementFailed:(locationRequirement:HomeViewModel.LocationRequirement)->Unit,
    checkLocationSettings:(locationSettingsLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>, onLocationEnabled: () -> Unit)->Unit,
){

    when(locationRequirements.value.firstOrNull()){
        HomeViewModel.LocationRequirement.PermissionNeeded -> {
            LocationPermission{ permissionGranted ->
                if(permissionGranted){
                    onRequirementMet(HomeViewModel.LocationRequirement.PermissionNeeded)
                }else{
                    onRequirementFailed(HomeViewModel.LocationRequirement.PermissionNeeded)
                }
            }

        }
        HomeViewModel.LocationRequirement.LocationActive -> {
            ActiveLocation(
                onActiveLocationResult = { locationActive->
                    if(locationActive) {
                        onRequirementMet(HomeViewModel.LocationRequirement.LocationActive)
                    }else{
                        onRequirementFailed(HomeViewModel.LocationRequirement.LocationActive)
                    }
                },
                checkLocationSettings = checkLocationSettings
            )
        }
        null -> {
            //no action
        }
    }
}



@Composable
private fun Map(
    reservedCarLocationState: State<Location?>,
    cameraLocation:State<Location?>,
    state: State<HomeViewModelState>,
    directionsState: State<DirectionsState>,
    onCarSelected:(car:Car)->Unit,
){

    val cameraPositionState = rememberCameraPositionState()

    val isMyLocationEnabled by remember{
        derivedStateOf { (cameraLocation.value != null)}
    }

    MapCameraPosition(
        cameraPositionState = cameraPositionState,
        location = cameraLocation
    )

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = isMyLocationEnabled
        ),

        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false
        )
    ) {
        MapContent(
            reservedCarLocationState =reservedCarLocationState ,
            homeViewModelStateCompose = state,
            directionsState = directionsState
        ){
            onCarSelected(it)
        }
    }
}
@Composable
private fun MapCameraPosition(
    cameraPositionState:CameraPositionState,
    location: State<Location?>,
){


    val cameraPosition = location.value ?: return
    cameraPositionState.position = CameraPosition.fromLatLngZoom(
        LatLng(
            cameraPosition.latitude,
            cameraPosition.longitude
        ), 15f
    )
}


@Composable
private fun MapContent(
    reservedCarLocationState:State<Location?>,
    homeViewModelStateCompose : State<HomeViewModelState>,
    directionsState:State<DirectionsState>,
    onMarkerClicked: (car: Car) -> Unit
){

    val reservedCarLocation = reservedCarLocationState.value
    val homeViewModelState = homeViewModelStateCompose.value
    when{
        reservedCarLocation != null ->{
            ReservedCarMarker(carLocation = reservedCarLocation)
        }
        homeViewModelState is HomeViewModelState.Success -> {
            NearbyCarsMarkers(
                nearbyCars = homeViewModelState.data,
                onMarkerClicked = onMarkerClicked
            )
            Directions(
                directionsState = directionsState
            )
        }


    }
}

@Composable
private fun Directions(
    directionsState:State<DirectionsState>,
){
    val stateValue = directionsState.value
    if(stateValue is DirectionsState.Success ) {
        stateValue.directions.forEach {
            DirectionOnMap(directionStep = it)
        }
    }
}

@Composable
private fun DirectionOnMap(directionStep: DirectionStep){
    Polyline(
        points = listOf(
            directionStep.startLocation,
            directionStep.endLocation
        ),
        color = Color.Blue,
        jointType = JointType.ROUND
    )
}






@Composable
private fun LocationState(
    locationState: State<HomeViewModel.LocationState>
){
    when(locationState.value){
        is  HomeViewModel.LocationState.Loading -> {
            LoadingSnackbar(
                text = stringResource(id = R.string.screen_home_loading_location)
            )
        }
        is HomeViewModel.LocationState.Unknown -> {
            Snackbar {
                Text(text = "We could not determine your location ")
            }
        }
        else -> {
            //no action
        }
    }
}



@Composable
private fun LocationPermission(
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
private fun ReservedCarMarker(
    carLocation:Location
){
    val markerState = rememberMarkerState(
        position = LatLng(
            carLocation.latitude,
            carLocation.longitude
        )
    )
    Marker(
        state = markerState,
        icon =  bitmapDescriptorFromVector(
            context = LocalContext.current,
            vectorResId = R.drawable.ic_car
        )
    )
}

@Composable
private fun NearbyCarsMarkers(
    nearbyCars: List<CarWithLocation>,
    onMarkerClicked:(car:Car)-> Unit
){
    nearbyCars.forEach {carWithLocation ->
        val markerState = rememberMarkerState(
            key = carWithLocation.car.id.toString(),
            position = carWithLocation.location
        )
        Marker(
            state = markerState,
            onClick = {
                onMarkerClicked(carWithLocation.car)
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
    carSelected: State<Car?>,
    selectedCarState: State<SelectedCarState>,
    reservationTimeLeft: State<Duration>,
    unlockPaymentState: State<UnlockPaymentState>,
    reservationStateListener: ReservationStateListener,
    content: @Composable ()->Unit
){

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )
    val carToPreview:State<Car?> = remember {
        derivedStateOf {
            val carReservationValue =  selectedCarState.value
            when{
                carReservationValue is SelectedCarState.Reserved -> {
                    carReservationValue.car
                }
                carSelected.value != null -> {
                    carSelected.value
                }
                else -> null
            }
        }
    }

    LaunchedEffect(carToPreview.value ){
        if(carToPreview.value != null) {
            bottomSheetScaffoldState.bottomSheetState.expand()
        }
    }


    BottomSheetScaffold(
        modifier = Modifier.padding(bottom = Dimens.medium.dp),
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            BottomSheetLayout {
                val car = carToPreview.value
                if(car != null){
                    CarDetails(
                        modifier = Modifier.padding(
                            horizontal = Dimens.medium.dp
                        ),
                        car = car
                    )
                    ReservationState(
                        unlockPaymentState = unlockPaymentState,
                        reservationTimeLeft = reservationTimeLeft,
                        selectedCarState = selectedCarState,
                        currentPreviewedCar =car ,
                        reservationStateListener =  reservationStateListener
                    )

                }else{
                    NoCarSelected(
                        modifier = Modifier.padding(
                            top = Dimens.tiny.dp
                        ),
                    )
                }
            }
        }, sheetPeekHeight = 36.dp
    ){
        content()
    }
}



@Composable
private fun ReservationState(
    unlockPaymentState: State<UnlockPaymentState>,
    reservationTimeLeft : State<Duration>,
    selectedCarState:State<SelectedCarState>,
    currentPreviewedCar: Car,
    reservationStateListener: ReservationStateListener,
){

    val reservation = selectedCarState.value
    if(reservation is SelectedCarState.Reserved){
        ReservationTimeLeft(
            timeLeft = reservationTimeLeft
        )
    }
    UnlockCarPayment(
        unlockPaymentState = unlockPaymentState ,
        reservationStateListener = reservationStateListener
    )



    when (reservation) {
        is SelectedCarState.Default -> {
            PricePerMinute(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.small.dp),
                price = currentPreviewedCar.pricePerMinute
            )
            ReserveButton(
                modifier = Modifier
                    .padding(Dimens.medium.dp)
            ) {
                reservationStateListener.reserveCar(currentPreviewedCar)
            }
        }
        is SelectedCarState.InProgress -> {
            LinearProgressIndicator(
                modifier = Modifier.padding(
                    horizontal = Dimens.medium.dp
                )
            )
        }
        is SelectedCarState.Reserved -> {
            CancelReservationButton(
                modifier = Modifier.padding(
                    horizontal = Dimens.medium.dp
                ), cancelReservation =  reservationStateListener::cancelReservation )

        }
        is SelectedCarState.UnlockingCar -> {
            LoadingAlert(
                text = stringResource(R.string.screen_home_redirecting)
            )
        }

        else -> {
            //no action
        }
    }
}


@Composable
private fun UnlockCarPayment(
    unlockPaymentState: State<UnlockPaymentState>,
    reservationStateListener: ReservationStateListener

) {
    when (val stateValue = unlockPaymentState.value) {
        is UnlockPaymentState.ReadyForUnlockUnlockPayment -> {
            PaymentButton(
                modifier = Modifier.padding(
                    horizontal = Dimens.medium.dp
                ), pay = reservationStateListener::payUnlockFee
            )
        }
        is UnlockPaymentState.LoadingUnlockPaymentData -> {
            LinearProgressIndicator(
                modifier = Modifier.padding(
                    horizontal = Dimens.medium.dp
                )
            )
        }
        is UnlockPaymentState.UnlockPaymentDataReady -> {
            PaymentButton(
                modifier = Modifier.padding(
                    horizontal = Dimens.medium.dp
                )
            ) {
                reservationStateListener.onPaymentDataReady(stateValue.paymentResponse)
            }

            reservationStateListener.onPaymentDataReady(stateValue.paymentResponse)
        }

        else -> {}
    }
}




@Composable
private fun PaymentButton(
    modifier:Modifier = Modifier,
    pay:()->Unit
){
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = pay
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
    timeLeft:State<Duration>
){
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        val elapsedTime  = DateUtils.formatElapsedTime(timeLeft.value.inWholeSeconds)
        Text(
            text = "Time left: $elapsedTime",
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Row(
            modifier= Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Divider(
                modifier = Modifier
                    .width(60.dp)
                    .padding(top = Dimens.small.dp),
                thickness = Dimens.tiny.dp
            )
        }
        content()
    }
}

@Composable
private fun NoCarSelected(
    modifier: Modifier = Modifier
){
    Text(
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = Color.Black,
        text = stringResource(R.string.screen_home_select_car),
        fontWeight = FontWeight.SemiBold,
        fontSize = Dimens.large.sp
    )
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
            .height(100.dp),
        horizontalArrangement = Arrangement.Start
    ) {

        CarImage(
            modifier = Modifier.padding(vertical = Dimens.small.dp),
            url = car.model.image.url
        )

        CarModelAndMake(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = Dimens.medium.dp),
            model = car.model.name,
            make = car.model.manufacturerName
        )
    }
}

@Composable
private fun CarModelAndMake(
    modifier:Modifier = Modifier,
    model:String,
    make:String
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = make,
            color = Color.Black
        )
        Text(
            text = model,
            color = Color.Black
        )
    }
}


@Composable
private fun CarImage(
    modifier:Modifier,
    url:String
){
    AsyncImage(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(Dimens.medium.dp)),
        model = url,
        contentDescription =null
    )
}

@Composable
private fun PricePerMinute(
    modifier :Modifier = Modifier,
    price:Double
){
    Text(
        modifier = modifier,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.SemiBold,
        fontSize = Dimens.large.sp,
        color = Color.Black,
        text = stringResource(R.string.screen_home_price_per_minute, price/100)
    )
}
