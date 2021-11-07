package com.andrei.car_rental_android.screens.SignIn

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.ui.Dimens
import com.andrei.car_rental_android.ui.composables.TextFieldErrorMessage
import com.andrei.car_rental_android.ui.theme.LoginBackgroundColor

@Composable

fun SignInScreen(navController: NavController){
        MainUI()
}
@Composable
fun MainUI(){
    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
        MainColumn()
    }
}

@Composable
fun MainColumn() {
    val loginViewModel = hiltViewModel<LoginViewModelImpl>()
    val loginUIState = loginViewModel.loginUiState.collectAsState()

      Box(modifier = Modifier.background(LoginBackgroundColor)) {
        BottomedCenteredColumn {
            UsernameTextField(
                viewModel = loginViewModel,
                modifier = Modifier.padding(bottom = Dimens.medium.dp)
            )
            PasswordTextField(
                viewModel = loginViewModel,
                modifier = Modifier.padding(bottom = Dimens.large.dp)
            )
            WrapperSignInButton(loginUIState = loginUIState)
        }
          val dialogOpened = mutableStateOf(loginUIState.value is LoginViewModel.LoginUIState.InvalidCredentials)
          InvalidCredentialsDialog(dialogOpened = dialogOpened){
              loginViewModel.resetUIState()
          }

    }
}

@Composable
fun BottomedCenteredColumn(content: @Composable ()->Unit ){
    Column(modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth()
        .padding(Dimens.huge.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom){
            content()
        }
}

@Composable
fun UsernameTextField(viewModel: LoginViewModel, modifier: Modifier = Modifier){
    val usernameState = viewModel.usernameState.collectAsState()

    val focusManager = LocalFocusManager.current

    TextField(
        maxLines = 1,
        modifier = modifier.fillMaxWidth(),
        value = usernameState.value,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = {
          focusManager.moveFocus(FocusDirection.Down)
        }),
        onValueChange ={
            viewModel.setUsername(it)
        },
        label = {
            Text(text = stringResource(R.string.screen_sign_in_email))
        }
    )
}

@Composable
fun PasswordTextField(viewModel: LoginViewModel, modifier: Modifier = Modifier){
    val passwordState = viewModel.passwordState.collectAsState()
    val focusManager = LocalFocusManager.current
    val passwordValidationState = viewModel.passwordValidationState.collectAsState().value
    Column {
        TextField(
            maxLines = 1,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus(true)
            }),
            modifier = modifier.fillMaxWidth(),
            value = passwordState.value,
            onValueChange = {
                viewModel.setPassword(it)
            },
            label = {
                Text(text = stringResource(id = R.string.screen_sign_in_password))
            },
            trailingIcon = {
                if (passwordValidationState is LoginViewModel.ValidationStateField.Error) {
                    Text(text = stringResource(R.string.screen_sign_in_password))
                }
            }
        )
        if (passwordValidationState is LoginViewModel.ValidationStateField.Error) {
            TextFieldErrorMessage(passwordState.value)
        }
    }
}


@Composable
fun WrapperSignInButton(loginUIState: State<LoginViewModel.LoginUIState>){
    val context = LocalContext.current
    when(loginUIState.value){
        is LoginViewModel.LoginUIState.Loading -> {
            CircularProgressIndicator()
        }
        is LoginViewModel.LoginUIState.Default -> {
            SignInButton()
        }
        is LoginViewModel.LoginUIState.LoggedIn -> {
            Toast.makeText(context,"Logged in ",Toast.LENGTH_SHORT).show()
            SignInButton()
        }
        is LoginViewModel.LoginUIState.InvalidCredentials -> {
            SignInButton()
        }
        is LoginViewModel.LoginUIState.ServerError -> {
            Toast.makeText(context,"Server error",Toast.LENGTH_SHORT).show()
            SignInButton()
        }

    }
}
@Composable
fun SignInButton(modifier: Modifier = Modifier){
    val viewModel = hiltViewModel<LoginViewModelImpl>()
    Button(modifier = modifier
        .fillMaxWidth()
        .padding(
            vertical = Dimens.huge.dp
        ),
        shape = MaterialTheme.shapes.large,
        onClick = {
            viewModel.login()
        }) {
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = Dimens.medium.sp,
            color = MaterialTheme.colors.secondary,
            modifier = Modifier.padding(vertical = Dimens.small.dp),
            text = stringResource(R.string.screen_sign_in_login)
        )
    }
}
@Composable
fun InvalidCredentialsDialog(dialogOpened: MutableState<Boolean>, onDismiss: ()-> Unit){
    if(dialogOpened.value) {
        AlertDialog(
            modifier = Modifier.background(Color.Black),
            onDismissRequest = {
                dialogOpened.value = false
            },
            title = {
                Text(text = stringResource(R.string.screen_sign_in_invalid_credentials_dialog_title))
            },
            text ={
                Text(text = stringResource(id = R.string.screen_sign_in_invalid_credentials_dialog_content))
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = {
                         onDismiss()
                    }) {
                        Text(
                            text = stringResource(id = R.string.screen_sign_in_invalid_credentials_dialog_positive_bt)
                        )
                    }
                }
            }
        )
}
}


