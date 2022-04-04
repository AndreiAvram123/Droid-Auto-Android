package com.andrei.car_rental_android.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


interface LocationHelper{
    val highPrecisionLowIntervalRequest:LocationRequest
    val highPrecisionHighIntervalRequest:LocationRequest
    fun checkLocationSettings(locationSettingsLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,onLocationEnabled: () -> Unit)
    fun requestLocationUpdates(locationRequest: LocationRequest)
    fun stopLocationUpdates()
     val lastKnownLocation:StateFlow<Location?>
}

class LocationHelperImpl(
    context:Context,
) : LocationHelper {

    override val highPrecisionLowIntervalRequest: LocationRequest  = LocationRequest.create().apply {
        interval = 1000
        fastestInterval = 500
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        smallestDisplacement = 3f
    }
    override val highPrecisionHighIntervalRequest: LocationRequest = LocationRequest.create().apply {
        interval = 6000
        fastestInterval = 4000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        smallestDisplacement = 3f

    }

    private val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(highPrecisionLowIntervalRequest)
        .build()

    private val  client = LocationServices.getSettingsClient(context)
    private val locationClient  =   LocationServices.getFusedLocationProviderClient(context)
    override val lastKnownLocation: MutableStateFlow<Location?> = MutableStateFlow(null)

    private val locationCallback:LocationCallback = object:LocationCallback(){

        override fun onLocationResult(locationResult: LocationResult) {
            lastKnownLocation.tryEmit(locationResult.lastLocation)
        }
    }


    @SuppressLint("MissingPermission")
     fun getLastKnownLocation(
        onLocationResolved:()->Unit,
        onError: ()-> Unit
    ){

        val cancellationTokenSource = CancellationTokenSource()
        locationClient.getCurrentLocation(
            android.location.LocationRequest.QUALITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location: Location? ->
            if(location != null){
                lastKnownLocation.tryEmit(location)
                onLocationResolved()
            }else{
                onError()

            }
        }.addOnFailureListener {
            onError()
        }

    }

    override fun checkLocationSettings(
        locationSettingsLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
        onLocationEnabled:()->Unit){
        client.checkLocationSettings(builder).addOnSuccessListener {
           onLocationEnabled()
        }.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    locationSettingsLauncher.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun requestLocationUpdates(locationRequest: LocationRequest) {
          stopLocationUpdates()

          locationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
    }

    override fun stopLocationUpdates() {
   //    locationClient.removeLocationUpdates(locationCallback)
    }


}
