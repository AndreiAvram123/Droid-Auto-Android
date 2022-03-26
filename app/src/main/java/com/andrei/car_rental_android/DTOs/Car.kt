package com.andrei.car_rental_android.DTOs

import com.google.android.gms.maps.model.LatLng

data class Car(
     var id: Long = 0,
     var model:CarModel,
     val location: LatLng
)
