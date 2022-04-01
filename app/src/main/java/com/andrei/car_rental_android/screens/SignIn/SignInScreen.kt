package com.andrei.car_rental_android.screens.SignIn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.composables.AlertDialogArgs
import com.andrei.car_rental_android.composables.CarRentalDialog
import com.andrei.car_rental_android.composables.TextFieldLabel
import com.andrei.car_rental_android.screens.SignIn.navigation.SignInNavigator
import com.andrei.car_rental_android.screens.SignIn.navigation.SignInNavigatorImpl
import com.andrei.car_rental_android.screens.register.base.RegisterScreenSurface
import com.andrei.car_rental_android.ui.Dimens
import com.andrei.car_rental_android.ui.composables.ButtonText


@Composable
fun SignInScreen(navController: NavController) {
    val navigator = SignInNavigatorImpl(navController)
    RegisterScreenSurface{
        MainContent(navigator = navigator)
    }
}



@Composable
fun MainContent(
    navigator: SignInNavigator
) {


    val loginViewModel = hiltViewModel<LoginViewModelImpl>()
    val loginUIState = loginViewModel.loginUiState.collectAsState()
    Box(modifier = Modifier.background(Color.White)) {
        BottomColumn {
            EmailTextField(
                viewModel = loginViewModel,
                modifier = Modifier.padding(bottom = Dimens.medium.dp)
            )
            PasswordTextField(
                viewModel = loginViewModel,
                modifier = Modifier.padding(bottom = Dimens.large.dp)
            )

            val isAuthenticating = remember {
                derivedStateOf {
                    loginUIState.value is LoginViewModel.LoginUIState.Loading
                }
            }

             AuthenticationButtons(
                 isAuthenticatingState = isAuthenticating, performLogin = {
                 loginViewModel.login()
             }, navigateToRegister = {
                 navigator.navigateToRegister()
             })

        }

        val dialogOpened = remember {
            derivedStateOf {
                loginUIState.value is LoginViewModel.LoginUIState.InvalidCredentials
            }
        }
        CarRentalDialog(
            dialogOpened = dialogOpened,
            alertDialogArgs = AlertDialogArgs(
                title = stringResource(R.string.screen_sign_in_invalid_credentials_dialog_title),
                text = stringResource(R.string.screen_sign_in_invalid_credentials_dialog_content),
                confirmButtonText = stringResource(R.string.screen_sign_in_invalid_credentials_dialog_positive_bt),
                onDismiss = {
                    loginViewModel.resetUIState()
                }
            )
        )

    }
}


@Composable
private fun BottomColumn(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(Dimens.huge.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        content()
    }
}



@Composable
private fun EmailTextField(viewModel: LoginViewModel, modifier: Modifier = Modifier) {
    val usernameState = viewModel.usernameState.collectAsState()

    val focusManager = LocalFocusManager.current

    TextField(
        maxLines = 1,
        modifier = modifier.fillMaxWidth(),
        value = usernameState.value,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = {
            focusManager.moveFocus(FocusDirection.Down)
        }),
        onValueChange = {
            viewModel.setUsername(it)
        },
        label = {
            TextFieldLabel(text = stringResource(R.string.screen_sign_in_email))
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent
        ),
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Email, contentDescription = null)
        },
    )
}


@Composable
fun PasswordTextField(viewModel: LoginViewModel, modifier: Modifier = Modifier) {
    val passwordState = viewModel.passwordState.collectAsState()
    val focusManager = LocalFocusManager.current

    Column {
        TextField(
            maxLines = 1,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus(true)
            }),
            modifier = modifier.fillMaxWidth(),
            value = passwordState.value,
            onValueChange = {
                viewModel.setPassword(it)
            },
            label = {
                TextFieldLabel(text = stringResource(R.string.screen_sign_in_password))
            },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Password, contentDescription = null)
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent
            )
        )

    }
}

@Composable
private fun AuthenticationButtons(
    isAuthenticatingState:State<Boolean>,
    performLogin:()->Unit,
    navigateToRegister: () -> Unit
){
    if(isAuthenticatingState.value){
        CircularProgressIndicator()
    }else{
        SignInButton(Modifier.padding(top = Dimens.medium.dp)){
            performLogin()
        }
        RegisterButton(Modifier.padding(top = Dimens.medium.dp)) {
            navigateToRegister()
        }
    }
}

@Composable
private fun SignInButton(
    modifier: Modifier = Modifier,
    login: () -> Unit,
) {
    Button(modifier = modifier
        .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        onClick = {
            login()
        }) {
         ButtonText(text = stringResource(id = R.string.screen_sign_in_login))
    }
}

@Composable
private fun RegisterButton(
    modifier: Modifier = Modifier,
    navigateToRegister: () -> Unit
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        onClick = {
            navigateToRegister()
        }) {
        ButtonText(text = stringResource(id = R.string.screen_sign_in_register))
    }
}



