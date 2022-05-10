package com.andrei.car_rental_android.screens.verification

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.andrei.car_rental_android.BuildConfig
import com.andrei.car_rental_android.state.SessionUserState
import com.withpersona.sdk2.inquiry.Environment
import com.withpersona.sdk2.inquiry.Inquiry

@Composable
fun DocumentVerificationScreen(){

    val viewModel = hiltViewModel<DocumentVerificationViewModelImpl>();
    val verificationTemplateID = BuildConfig.VERIFICATION_TEMPLATE_ID

    val inquiryResult = rememberLauncherForActivityResult(
        contract = Inquiry.Contract()){ result-> viewModel.setInquireResponse(result)
    }

    val sessionUserState = viewModel.sessionUserState.collectAsState().value
    LaunchedEffect(sessionUserState) {
        if(sessionUserState is SessionUserState.Loaded){
            val inquiry = Inquiry.fromTemplate(verificationTemplateID)
                .environment(Environment.SANDBOX)
                .referenceId(sessionUserState.user.id.toString())
                .build()

            inquiryResult.launch(inquiry)
    }


}

}
