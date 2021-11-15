package com.andrei.car_rental_android.screens.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.composables.TextFieldLabel
import com.andrei.car_rental_android.navigation.RegistrationScreen
import com.andrei.car_rental_android.screens.register.base.RegisterScreenSurface
import com.andrei.car_rental_android.ui.Dimens



@Composable
fun UserNameScreen(navController: NavController) {
    MainContent(
        navigateToNextScreen = {
            navController.navigate(RegistrationScreen.EmailScreen.screenName)
        }

    )
}

@Preview
@Composable
private fun MainContent(navigateToNextScreen:()->Unit = {}) {
   RegisterScreenSurface {

        val viewModel : UsernameViewModel = hiltViewModel<UsernameViewModelImpl>()

        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeadings()
            Fields(
                viewModel = viewModel
            )
        }
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                ContinueButton(
                    enabled = viewModel.nextButtonEnabled.collectAsState(),
                    onClick = {
                       navigateToNextScreen()
                    }
                )
            }
        }
    }
}


@Composable
private fun ScreenHeadings(modifier: Modifier = Modifier){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = Dimens.large.dp
            ),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(id = R.string.screen_user_name_heading_1))
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(id = R.string.screen_user_name_heading_2))
    }
}

@Composable
private fun Fields(modifier: Modifier = Modifier,
                  viewModel: UsernameViewModel){
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FirstNameField(
            state = viewModel.firstName.collectAsState(),
            onValueChanged = {
                viewModel.setFirstName(it)
            }
        )
        SurnameField(modifier = Modifier.padding(top = Dimens.medium.dp),
            state = viewModel.surname.collectAsState(),
            onValueChanged = {
                viewModel.setSurname(it)
            }
        )
    }
}

@Composable
fun SurnameField(modifier: Modifier = Modifier,
                 state:State<String>,
                 onValueChanged : (newValue:String) -> Unit){
    val focusManager = LocalFocusManager.current

    TextField(
        singleLine= true,
        modifier = modifier.fillMaxWidth(),
        value = state.value,
        onValueChange = {
            onValueChanged(it)
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions =  KeyboardActions(onNext = {
            focusManager.clearFocus(true)
        } ),
        label = {
            TextFieldLabel(text = stringResource(R.string.screen_user_name_surname))
        },
    )
}

@Composable
fun FirstNameField(modifier:Modifier = Modifier,
                   state:State<String>,
                   onValueChanged : (newValue:String) -> Unit){
    val focusManager = LocalFocusManager.current

    TextField(
        singleLine= true ,
        modifier = modifier.fillMaxWidth(),
        value = state.value,
        onValueChange = {
            onValueChanged(it)
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Next
        ),
        keyboardActions =  KeyboardActions(onNext = {
            focusManager.moveFocus(FocusDirection.Down)
        } ),
        label = {
            TextFieldLabel(text = stringResource(R.string.screen_user_name_first_name))
        },
    )

}

@Composable
private fun ContinueButton(modifier: Modifier = Modifier,
                           enabled:State<Boolean>,
                            onClick:()->Unit){
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = {
          onClick()
        },
        enabled = enabled.value
    ) {
        Text(text = stringResource(R.string.screen_user_name_continue))
    }
}