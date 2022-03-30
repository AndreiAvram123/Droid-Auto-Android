package com.andrei.car_rental_android.helpers

import android.content.Context
import android.content.IntentSender
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest


interface LocationSettingsHelper{
    val highPrecisionRequest:LocationRequest
    fun checkLocationSettings(onLocationActive: () -> Unit)
}

class LocationSettingsHelperImpl(
    private val context:Context,
    private val locationSettingsLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
) : LocationSettingsHelper {

    override val highPrecisionRequest: LocationRequest  = LocationRequest.create().apply {
        interval = 1000
        fastestInterval = 500
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(highPrecisionRequest)
        .build()

    private val  client = LocationServices.getSettingsClient(context)




    override fun checkLocationSettings(onLocationEnabled:()->Unit){
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


}
