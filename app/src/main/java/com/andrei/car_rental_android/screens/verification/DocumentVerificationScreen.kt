package com.andrei.car_rental_android.screens.verification

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.andrei.car_rental_android.BuildConfig
import com.andrei.car_rental_android.ui.composables.ButtonText
import com.withpersona.sdk2.inquiry.Environment
import com.withpersona.sdk2.inquiry.Inquiry
import com.withpersona.sdk2.inquiry.InquiryResponse


@Composable
fun DocumentVerificationScreen(){
    val verificationTemplateID = BuildConfig.VERIFICATION_TEMPLATE_ID
    val inquiryResult = rememberLauncherForActivityResult(
        contract = Inquiry.Contract()){ result->
        when (result) {
            is InquiryResponse.Complete ->{

            }
            is InquiryResponse.Cancel -> {
                // ... abandoned flow
            }
            is InquiryResponse.Error -> {
                // ... something went wrong
            }
        }
    }

    Column {

        Text(
            text = "We do believer it's you, but we need to be sure.."
        )
        Button(onClick = {
            val inquiry = Inquiry.fromTemplate(verificationTemplateID)
                .environment(Environment.SANDBOX)
                .build()

            inquiryResult.launch(inquiry)
        }) {
            ButtonText(text = "Start verification process")
        }
    }



}