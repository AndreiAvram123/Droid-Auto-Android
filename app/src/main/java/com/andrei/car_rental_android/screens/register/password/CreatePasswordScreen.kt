package com.andrei.car_rental_android.screens.register.password

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.screens.register.base.RegisterScreenSurface
import com.andrei.car_rental_android.ui.Dimens

@Composable
fun CreatePasswordScreen(navController: NavController){
   MainContent(navigateForward = {

   })
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun MainContent(navigateForward:()-> Unit = {}){
    RegisterScreenSurface {
       TopContent()
       CenterContent()
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
private fun CenterContent(){
    val viewModel = hiltViewModel<CreatePasswordViewModelImpl>()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ){
        PasswordTextField(
            state = viewModel.passwordState.collectAsState(),
            onValueChanged = {
                viewModel.setPassword(it)
            }
        )
        PasswordStrengthIndicators(
            passwordStrengthState = viewModel.passwordStrength.collectAsState()
        )

    }
}


@Composable
private fun PasswordTextField(
    modifier: Modifier = Modifier,
    state: State<String>,
    onValueChanged:(newValue:String)->Unit,
){

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = state.value ,
        onValueChange = onValueChanged,
        placeholder = {
            Text(text = stringResource(R.string.screen_password_enter_password_here))
        }
    )
}

@Composable
private fun PasswordStrengthIndicators(
    modifier: Modifier = Modifier,
    passwordStrengthState:State<List<CreatePasswordViewModel.PasswordStrengthCriteria>?>,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Dimens.medium.dp),
        horizontalAlignment = Alignment.Start
    ) {
        val passwordStrength = passwordStrengthState.value

        CreatePasswordViewModel.PasswordStrengthCriteria.values().forEach { criteria ->
            PasswordStrengthIndicator(
                text = stringResource(criteria.hintStringResource()),
                state = when {
                    passwordStrength == null -> PasswordIndicatorState.Default
                    passwordStrength.contains(criteria) -> PasswordIndicatorState.Valid
                    else -> PasswordIndicatorState.Invalid
                }
            )
        }

    }
}
fun CreatePasswordViewModel.PasswordStrengthCriteria.hintStringResource():Int{
    return when(this){
         CreatePasswordViewModel.PasswordStrengthCriteria.IncludesLowercaseLetter-> R.string.screen_password_include_lowercase_letter
         CreatePasswordViewModel.PasswordStrengthCriteria.IncludesUppercaseLetter-> R.string.screen_password_include_uppercase_letter
         CreatePasswordViewModel.PasswordStrengthCriteria.IncludesNumber -> R.string.screen_password_include_number
         CreatePasswordViewModel.PasswordStrengthCriteria.IncludesSpecialCharacter -> R.string.screen_password_include_special_character
         CreatePasswordViewModel.PasswordStrengthCriteria.IncludesMinNumberCharacters -> R.string.screen_password_include_8_characters
    }
}




@Composable
private fun PasswordStrengthIndicator(
    text:String,
    state:PasswordIndicatorState
){
    Row(modifier = Modifier.fillMaxWidth()) {
       PasswordStrengthCriteriaIcon(state = state)
        Text(text = text)
    }

}
@Composable
private fun PasswordStrengthCriteriaIcon(
    modifier: Modifier = Modifier,
    state: PasswordIndicatorState
){
    when(state){
        is PasswordIndicatorState.Default-> {
          //no icon
        }
        is PasswordIndicatorState.Valid->{
            Icon(modifier = modifier, imageVector = Icons.Filled.Check , contentDescription = null)
        }
        is PasswordIndicatorState.Invalid ->  {
            Icon(modifier = modifier, imageVector = Icons.Filled.Close, contentDescription = null)
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