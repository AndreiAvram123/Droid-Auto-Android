package com.andrei.car_rental_android.DTOs

import android.location.Location
import com.google.android.gms.maps.model.LatLng

data class Car(
     var id: Long = 0,
     var model:CarModel,
     val location: LatLng
)

fun LatLng.toLocation():Location{
     val location =  Location("")
     location.latitude = latitude
     location.longitude = longitude
     return location
}