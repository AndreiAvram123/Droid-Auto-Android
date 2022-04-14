package com.andrei.car_rental_android.screens.receipt

class ReceiptNavHelper {

    data class Args(
        val rideID: Long
    )

    companion object {
         private const val baseRoute = "receiptScreen"
         val route = "${baseRoute}?${Args::rideID.name}={rideID}"
    }
}