package com.andrei.car_rental_android.screens.Home

import android.graphics.Bitmap
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.andrei.car_rental_android.R
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
       MapView()
    }
}

@Composable
fun MapView() {
    val mapView = rememberMapWithLifecycle()
    val viewModel: HomeViewModel = hiltViewModel<HomeViewModelImpl>()
    val nearbyCars = viewModel.nearbyCars.collectAsState()
    val mapBitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val coroutineScope = rememberCoroutineScope()
            AndroidView(factory = {
                mapView
            }) {
                coroutineScope.launch {
                    val map = it.awaitMap()
                    map.uiSettings.isZoomControlsEnabled = true
                    if (nearbyCars.value.isNotEmpty()) {
                        map.moveCamera(CameraUpdateFactory.newLatLng(nearbyCars.value.first()))
                    }
                    nearbyCars.value.forEach { location ->
                        val marker = MarkerOptions().title("Soemthing").position(location)
                        map.addMarker(marker)
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