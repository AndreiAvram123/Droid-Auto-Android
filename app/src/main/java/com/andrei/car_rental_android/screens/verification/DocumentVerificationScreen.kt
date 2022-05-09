package com.andrei.car_rental_android.screens.verification

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.andrei.car_rental_android.BuildConfig
import com.withpersona.sdk2.inquiry.Environment
import com.withpersona.sdk2.inquiry.Inquiry

@Composable
fun DocumentVerificationScreen(){

    val viewModel = hiltViewModel<DocumentVerificationViewModelImpl>();
    val verificationTemplateID = BuildConfig.VERIFICATION_TEMPLATE_ID

    val inquiryResult = rememberLauncherForActivityResult(
        contract = Inquiry.Contract()){ result-> viewModel.setInquireResponse(result)
        }

    LaunchedEffect(true){
        val inquiry = Inquiry.fromTemplate(verificationTemplateID)
            .environment(Environment.SANDBOX)
            .build()

        inquiryResult.launch(inquiry)
    }

}
