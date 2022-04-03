package com.andrei.car_rental_android.screens.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.composables.TextFieldLabel
import com.andrei.car_rental_android.screens.register.base.BackButton
import com.andrei.car_rental_android.screens.register.base.CenterColumn
import com.andrei.car_rental_android.screens.register.base.ContinueButton
import com.andrei.car_rental_android.screens.register.base.RegisterScreenSurface
import com.andrei.car_rental_android.screens.register.firstNameLastName.FirstNameLastNameNavigator
import com.andrei.car_rental_android.screens.register.firstNameLastName.FirstNameLastNameNavigatorImpl
import com.andrei.car_rental_android.ui.Dimens



@Composable
fun NamesScreen(
    navController: NavController,
) {
    val navigator = FirstNameLastNameNavigatorImpl(navController)
    RegisterScreenSurface {
        MainContent(navigator = navigator)
    }

}

@Composable
private fun MainContent(
    navigator:FirstNameLastNameNavigator
) {
    val viewModel : FirstNameLastNameViewModel = hiltViewModel<FirstNameLastNameViewModelImpl>()

    TopSection(navigateBack = {
        navigator.navigateBack()
    })
        CenterSection(
            viewModel = viewModel
        )
        BottomSection(
            viewModel = viewModel,
            navigateForward = {
                navigator.navigateToPasswordScreen(
                    viewModel.firstName.value,
                    viewModel.lastName.value
                )
            }
        )
    }


@Composable
private fun BottomSection(
    viewModel: FirstNameLastNameViewModel,
    navigateForward:()->Unit
){
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        val nextButtonEnabled = viewModel.nextButtonEnabled.collectAsState().value
        if(nextButtonEnabled) {
           BeautifulName(
               modifier = Modifier.padding(vertical = Dimens.medium.dp)
           )
        }
            ContinueButton(
                enabled = nextButtonEnabled,
                onClick = {
                    navigateForward()
                }
            )

    }
}

@Composable
private fun BeautifulName(
    modifier:Modifier = Modifier
){
    Text(
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        fontSize = Dimens.medium.sp,
        text = stringResource(R.string.screen_names_beautiful_name)
    )
}


@Composable
private fun TopSection(
     navigateBack:()->Unit
){
    Column(modifier = Modifier.fillMaxSize()) {
        BackButton {
          navigateBack()
        }
        ScreenHeadings()

    }
}


@Composable
private fun ScreenHeadings(modifier: Modifier = Modifier){
    Column(
        modifier = modifier.padding(
            top = Dimens.large.dp
    )) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = Dimens.large.sp,
            text = stringResource(id = R.string.screen_names_heading1)
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = Dimens.large.sp,
            text = stringResource(id = R.string.screen_names_heading_2)
        )
    }
}

@Composable
private fun CenterSection(
    viewModel: FirstNameLastNameViewModel
){
    CenterColumn{
        FirstNameField(
            state = viewModel.firstName.collectAsState(),
            onValueChanged = {
                viewModel.setFirstName(it)
            }
        )
        SurnameField(modifier = Modifier.padding(top = Dimens.medium.dp),
            state = viewModel.lastName.collectAsState(),
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

    OutlinedTextField(
        singleLine= true,
        modifier = modifier.fillMaxWidth(),
        value = state.value,
        onValueChange = {
            onValueChanged(it.trim())
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions =  KeyboardActions(onNext = {
            focusManager.clearFocus(true)
        } ),
        label = {
            TextFieldLabel(text = stringResource(R.string.screen_names_surname))
        },
    )
}

@Composable
fun FirstNameField(modifier:Modifier = Modifier,
                   state:State<String>,
                   onValueChanged : (newValue:String) -> Unit){
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        singleLine= true ,
        modifier = modifier.fillMaxWidth(),
        value = state.value,
        onValueChange = {
            onValueChanged(it.trim())
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Next
        ),
        keyboardActions =  KeyboardActions(onNext = {
            focusManager.moveFocus(FocusDirection.Down)
        } ),
        label = {
            TextFieldLabel(text = stringResource(R.string.screen_names_first_name))
        },
    )

}