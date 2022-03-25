package com.andrei.car_rental_android.screens.Home

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.engine.request.RequestState
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = hiltViewModel<HomeViewModelImpl>()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        MapView(nearbyCarsState = viewModel.nearbyCars.collectAsState().value)
    }
}
@Composable
fun MapView(nearbyCarsState: RequestState<List<LatLng>>) {
    val mapView = rememberMapWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {

        AndroidView(factory = {
              mapView
            }) {
            it.getMapAsync { map ->
                when(nearbyCarsState){
                    is RequestState.Success ->{
                        map.uiSettings.isZoomControlsEnabled = true
                       nearbyCarsState.data.forEach {location ->
                           val marker = MarkerOptions().position(location)
                           map.addMarker(marker)
                       }
                    }
                    is RequestState.Loading ->{
                    }
                    else -> {

                    }
                }
            }
        }

    }


}
    @Composable
    fun rememberMapWithLifecycle(): MapView {
        val context = LocalContext.current
        val map = remember {
            MapView(context).apply {
                id = R.id.map
            }
        }
        val lifecycleObserver = rememberMapLifecycleObserver(map)
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        DisposableEffect(lifecycle) {
            lifecycle.addObserver(lifecycleObserver)
            onDispose {
                lifecycle.removeObserver(lifecycleObserver)
            }
        }
        return map
    }

    @Composable
    fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
        remember(mapView) {
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                    Lifecycle.Event.ON_START -> mapView.onStart()
                    Lifecycle.Event.ON_RESUME -> mapView.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                    Lifecycle.Event.ON_STOP -> mapView.onStop()
                    Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                    else -> throw IllegalStateException()
                }
            }
        }