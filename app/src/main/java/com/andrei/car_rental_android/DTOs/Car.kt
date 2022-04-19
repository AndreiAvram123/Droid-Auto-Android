package com.andrei.car_rental_android.DTOs

import android.location.Location
import com.google.android.gms.maps.model.LatLng

data class CarWithLocation(
     val car :Car,
     val location:LatLng
)

data class Car(
     var id: Long = 0,
     var model:CarModel,
     //prince per minute in the smallest currency unit . In this case pence
     val pricePerMinute:Long
)

fun LatLng.toAndroidLocation():Location{
     val location =  Location("")
     location.latitude = latitude
     location.longitude = longitude
     return location
}