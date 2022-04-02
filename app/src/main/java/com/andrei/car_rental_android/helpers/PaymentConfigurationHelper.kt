package com.andrei.car_rental_android.helpers

import com.stripe.android.paymentsheet.PaymentSheet

object PaymentConfigurationHelper {

    private  fun getGooglePlayConfiguration():PaymentSheet.GooglePayConfiguration = PaymentSheet.GooglePayConfiguration(
            environment = PaymentSheet.GooglePayConfiguration.Environment.Test,
            countryCode = "UK"
        )

    fun buildConfiguration() =
        PaymentSheet.Configuration.Builder("car-rental")
            .googlePay(PaymentConfigurationHelper.getGooglePlayConfiguration())
            .build()
    }