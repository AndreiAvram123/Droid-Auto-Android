package com.andrei.car_rental_android.engine.response

import com.google.android.gms.maps.model.LatLng

data class DirectionStep(
     val endLocation:LatLng,
     val startLocation:LatLng
)