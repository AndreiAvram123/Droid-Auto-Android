package com.andrei.car_rental_android.screens.SignIn

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.composables.TextFieldLabel
import com.andrei.car_rental_android.ui.Dimens
import com.andrei.car_rental_android.ui.composables.TextFieldErrorMessage

@Composable

fun SignInScreen(navController: NavController){
    MainUI()
    Button(onClick ={
        navController.navigate("register")
    } ) {

        Text(text = "navigate")
    }
}
@Composable
@Preview(showSystemUi = true, showBackground = true)
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

    Box(modifier = Modifier.background(Color.White)) {
        BottomedCenteredColumn {
            EmailTextField(
                viewModel = loginViewModel,
                modifier = Modifier.padding(bottom = Dimens.medium.dp)
            )
            PasswordTextField(
                viewModel = loginViewModel,
                modifier = Modifier.padding(bottom = Dimens.large.dp)
            )
            WrapperSignInButton(loginUIState = loginUIState)
        }

        InvalidCredentialsDialog(loginUIState = loginUIState){
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
fun EmailTextField(viewModel: LoginViewModel, modifier: Modifier = Modifier){
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
                TextFieldLabel(text = stringResource(R.string.screen_sign_in_password))
            },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Password, contentDescription = null)
            },
            trailingIcon = {
                if (passwordValidationState is LoginViewModel.ValidationStateField.Error) {
                    Text(text = stringResource(R.string.screen_sign_in_password))
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent
            )
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
            modifier = Modifier.padding(vertical = Dimens.small.dp),
            text = stringResource(R.string.screen_sign_in_login)
        )
    }
}
@Composable
fun InvalidCredentialsDialog(loginUIState: State<LoginViewModel.LoginUIState>, onDismiss: ()-> Unit){
    var dialogOpened by  remember {
        mutableStateOf(false)
    }
    dialogOpened = loginUIState.value is LoginViewModel.LoginUIState.InvalidCredentials

    if(dialogOpened) {
        AlertDialog(
            backgroundColor = MaterialTheme.colors.surface,
            onDismissRequest = {
                dialogOpened = false
            },
            title = {
                Text(
                    text = stringResource(R.string.screen_sign_in_invalid_credentials_dialog_title),
                    color = MaterialTheme.colors.onSurface
                )
            },
            text ={
                Text(
                    text = stringResource(id = R.string.screen_sign_in_invalid_credentials_dialog_content),
                    color = MaterialTheme.colors.onSurface
                )
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


