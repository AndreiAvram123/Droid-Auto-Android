package com.andrei.car_rental_android.helpers

import com.stripe.android.paymentsheet.PaymentSheet

object PaymentConfigurationHelper {

    private  fun googlePlayConfiguration():PaymentSheet.GooglePayConfiguration =
        PaymentSheet.GooglePayConfiguration(
            environment = PaymentSheet.GooglePayConfiguration.Environment.Test,
            countryCode = "UK"
        )

    private fun customerConfiguration(
        customerID:String,
        customerKey:String
    ):PaymentSheet.CustomerConfiguration = PaymentSheet.CustomerConfiguration(
        customerID,
        customerKey
    )


    fun buildConfiguration(
        customerID:String,
        customerKey:String
    ) = PaymentSheet.Configuration.Builder("car-rental")
            .customer(customerConfiguration(
                customerID = customerID,
                customerKey = customerKey
            )).googlePay(googlePlayConfiguration()).build()
    }