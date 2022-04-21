package com.andrei.car_rental_android.screens.register.Email

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.composables.TextFieldLabel
import com.andrei.car_rental_android.screens.register.Email.RegisterEmailViewModel.EmailValidationState.EmailValidationError
import com.andrei.car_rental_android.screens.register.base.BackButton
import com.andrei.car_rental_android.screens.register.base.ContinueButton
import com.andrei.car_rental_android.ui.Dimens
import com.andrei.car_rental_android.ui.composables.TextFieldErrorMessage


@Composable
fun RegisterEmailScreen(
    navController: NavController,
    registerEmailNavArgs: RegisterEmailNavHelper.RegisterEmailNavArgs
) {
    val navigator = RegisterEmailNavigatorImpl(
        navController = navController,
        navArgs = registerEmailNavArgs
    )
    MainContent(navigator = navigator)
}


@Composable
private fun MainContent(
    navigator:RegisterEmailNavigator
){
    val viewModel:RegisterEmailViewModel = hiltViewModel<RegisterEmailViewModelImpl>()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween

    ) {
        TopContent(navigateBack = navigator::navigateBack)
        CenterContent(viewModel = viewModel)
        BottomContent(
            viewModel = viewModel,
            navigateForward = {
                navigator.navigateToPasswordScreen(viewModel.email.value)
            }
        )
    }
}

@Composable
private fun TopContent(
    navigateBack:()->Unit
){
    Column {
        BackButton(navigateBack = navigateBack)
        Heading(text = stringResource(R.string.screen_email_heading1))
    }
}

@Composable
private fun Heading(
    modifier:Modifier = Modifier,
    text:String
){
    Text(
        modifier =modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        fontSize = Dimens.large.sp,
        text = text
    )
}

@Composable
private fun CenterContent(viewModel: RegisterEmailViewModel){
    Column(
        modifier = Modifier
            .padding(horizontal = Dimens.medium.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,

        ) {
        EmailTextFieldColumn(
            modifier = Modifier.fillMaxWidth(),
            state = viewModel.email.collectAsState(),
            onValueChanged ={
                viewModel.setEmail(it.trim())
            },
            validationState = viewModel.emailValidationState.collectAsState()
        )
    }
}
@Composable
private fun BottomContent(
    viewModel: RegisterEmailViewModel,
    navigateForward:() -> Unit
){
    Column(
        modifier = Modifier
            .padding(horizontal = Dimens.medium.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        ContinueButton(
            enabled = viewModel.nextButtonEnabled.collectAsState().value,
            onClick = {
                navigateForward()
            }
        )
    }
}


@Composable
private fun EmailTextFieldColumn(
    modifier: Modifier = Modifier,
    state: State<String>,
    onValueChanged: (newValue:String)-> Unit,
    validationState:State<RegisterEmailViewModel.EmailValidationState>
){
    val invalid = when(validationState.value){
        is EmailValidationError.EmailAlreadyTaken -> true
        is EmailValidationError.InvalidFormat -> true
        else -> false
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        FieldAboveLabel(
            modifier = Modifier.padding(
                bottom = Dimens.small.dp
            ),
            text = stringResource(R.string.screen_email_email_label_above)
        )
        OutlinedTextField(
            modifier = modifier,
            value = state.value,
            singleLine = true,
            onValueChange = {
                onValueChanged(it.trim())
            },
            isError = invalid,
            placeholder = {
                TextFieldLabel(text = stringResource(R.string.screen_email_email_label_field))
            },
        )
        EmailValidationError(validationState)
    }
}

@Composable
private fun FieldAboveLabel(
    modifier: Modifier = Modifier,
    text:String
){
    Text(
        modifier = modifier,
        text = text,
        fontSize = Dimens.big.sp,
    )
}
@Composable
fun EmailValidationError(
    validationState:State<RegisterEmailViewModel.EmailValidationState>
){
    if(validationState.value is EmailValidationError){
        val errorMessage = when(validationState.value){
            is EmailValidationError.InvalidFormat -> stringResource(
                R.string.screen_email_invalid_email_format
            )
            is EmailValidationError.EmailAlreadyTaken -> stringResource(
                R.string.screen_email_already_taken
            )
            else -> stringResource(R.string.screen_email_unknown_error)
        }
        TextFieldErrorMessage(errorMessage)
    }
}




