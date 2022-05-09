package com.andrei.car_rental_android.screens.register.password

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.screens.register.base.ContinueButton
import com.andrei.car_rental_android.screens.register.base.RegisterScreenSurface
import com.andrei.car_rental_android.ui.Dimens
import com.andrei.car_rental_android.ui.composables.TextFieldErrorMessage

@Composable
fun CreatePasswordScreen(
    navController: NavController,
    arguments : CreatePasswordNavHelper.CreatePasswordNavArgs
){
    val navigator = CreatePasswordNavigatorImpl(
        navController = navController,
        args = arguments
    )
   MainContent(navigator)

}

@Composable
private fun MainContent(
    navigator:CreatePasswordNavigator,
){
    val viewModel = hiltViewModel<CreatePasswordViewModelImpl>()
    RegisterScreenSurface {
       TopContent()
       CenterContent(viewModel = viewModel)
        BottomContent(nextButtonEnabled = viewModel.nextButtonEnabled.collectAsState() ) {
            navigator.navigateToCreatingAccountScreen(viewModel.password.value)
        }
    }
}



@Composable
private fun TopContent(){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Title()
    }
}


@Composable
private fun CenterContent(
    modifier:Modifier = Modifier,
    viewModel:CreatePasswordViewModel
) {


    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ){
        PasswordTextField(
            state = viewModel.password.collectAsState(),
            onValueChanged = {
                viewModel.setPassword(it.trim())
            }
        )
        PasswordStrengthIndicators(
            modifier = Modifier.padding(
                horizontal = Dimens.small.dp,
                vertical = Dimens.medium.dp
            ),
            passwordStrengthState = viewModel.passwordStrength.collectAsState()
        )
        ReenterPasswordTextField(
            modifier = Modifier.padding(top = Dimens.medium.dp),
            state = viewModel.reenteredPassword.collectAsState(),
            onValueChanged = {
                viewModel.setReenteredPassword(it.trim())
            },
            validation = viewModel.reenteredPasswordValidation.collectAsState()
        )
    }
}
@Composable
private fun BottomContent(
    modifier:Modifier = Modifier,
    nextButtonEnabled: State<Boolean>,
    navigateForward: () -> Unit
){
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        ContinueButton(enabled = nextButtonEnabled.value){
            navigateForward()
        }
    }
}



@Composable
private fun PasswordTextField(
    modifier: Modifier = Modifier,
    state: State<String>,
    onValueChanged:(newValue:String)->Unit
){

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = state.value ,
        singleLine = true,
        onValueChange = onValueChanged,
        placeholder = {
            Text(text = stringResource(R.string.screen_password_enter_password_here))
        },
        visualTransformation = PasswordVisualTransformation()
    )

}

@Composable
private fun ReenterPasswordTextField(
    modifier: Modifier = Modifier,
    state:State<String>,
    onValueChanged: (newValue: String) -> Unit,
    validation : State<CreatePasswordViewModel.ReenteredPasswordValidation>
){
    val isError by remember{
        derivedStateOf { validation.value is CreatePasswordViewModel.ReenteredPasswordValidation.Invalid }
    }

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = state.value,
        singleLine = true,
        onValueChange = onValueChanged,
        placeholder = {
            Text(text =  stringResource(R.string.screen_password_reenter_password))
        },
        visualTransformation = PasswordVisualTransformation(),
        isError = isError
    )
    if(isError){
        TextFieldErrorMessage(stringResource(R.string.screen_password_reenter_password_invalid))
    }
}

@Composable
private fun PasswordStrengthIndicators(
    modifier: Modifier = Modifier,
    passwordStrengthState:State<List<CreatePasswordViewModel.PasswordRequirement>>,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        val passwordStrength = passwordStrengthState.value

        CreatePasswordViewModel.PasswordRequirement.values().forEach { criteria ->
            PasswordStrengthIndicator(
                text = stringResource(getHintResourceForCriteria(criteria)),
                state = when {
                    passwordStrength.isEmpty() -> PasswordIndicatorState.Default
                    passwordStrength.contains(criteria) -> PasswordIndicatorState.Valid
                    else -> PasswordIndicatorState.Invalid
                }
            )
        }

    }
}
fun getHintResourceForCriteria(criteria:CreatePasswordViewModel.PasswordRequirement):Int{
    return when(criteria){
        CreatePasswordViewModel.PasswordRequirement.IncludesLowercaseLetter-> R.string.screen_password_include_lowercase_letter
        CreatePasswordViewModel.PasswordRequirement.IncludesUppercaseLetter-> R.string.screen_password_include_uppercase_letter
        CreatePasswordViewModel.PasswordRequirement.IncludesDigit -> R.string.screen_password_include_number
        CreatePasswordViewModel.PasswordRequirement.IncludesSpecialCharacter -> R.string.screen_password_include_special_character
        CreatePasswordViewModel.PasswordRequirement.IncludesMinNumberCharacters -> R.string.screen_password_include_8_characters
    }
}




@Composable
private fun PasswordStrengthIndicator(
    text:String,
    state:PasswordIndicatorState
){
    Row(modifier = Modifier
        .height(24.dp)
        .fillMaxWidth()) {
       PasswordStrengthCriteriaIcon(state = state)
       Text(
           text = text,
           fontSize = Dimens.medium.sp
       )
    }

}
@Composable
private fun PasswordStrengthCriteriaIcon(
    modifier: Modifier = Modifier,
    state: PasswordIndicatorState
){
    when(state){
        is PasswordIndicatorState.Default-> {

        }
        is PasswordIndicatorState.Valid->{
            Icon(modifier = modifier, imageVector = Icons.Filled.Check , contentDescription = null, tint = Color.Green )
        }
        is PasswordIndicatorState.Invalid ->  {
            Icon(modifier = modifier, imageVector = Icons.Filled.Close, contentDescription = null, tint = Color.Red)
        }
    }
}

sealed class PasswordIndicatorState{
    object Default:PasswordIndicatorState()
    object Valid:PasswordIndicatorState()
    object Invalid:PasswordIndicatorState()
}

@Composable
private fun Title(){
   Row(
       modifier = Modifier
           .fillMaxWidth()
           .padding(top = Dimens.large.dp),
       horizontalArrangement = Arrangement.Center
   ) {
       Text(
           text = stringResource(R.string.screen_password_choose_password_title),
           fontSize = Dimens.large.sp
       )
   }
}