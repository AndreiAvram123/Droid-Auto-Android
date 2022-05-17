package com.andrei.car_rental_android.screens.SignIn

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.composables.AlertDialogArgs
import com.andrei.car_rental_android.composables.CarRentalDialog
import com.andrei.car_rental_android.composables.TextFieldLabel
import com.andrei.car_rental_android.screens.SignIn.navigation.SignInNavigator
import com.andrei.car_rental_android.screens.SignIn.navigation.SignInNavigatorImpl
import com.andrei.car_rental_android.ui.Dimens
import com.andrei.car_rental_android.ui.composables.ButtonText


@Composable
fun SignInScreen(navController: NavController) {
    val navigator = SignInNavigatorImpl(navController)
    val loginViewModel = hiltViewModel<LoginViewModelImpl>()

    MainContent(
        navigator = navigator,
        loginViewModel = loginViewModel
    )

}



@Composable
fun MainContent(
    navigator: SignInNavigator,
    loginViewModel: LoginViewModel
) {

    val loginUIState = loginViewModel.loginUiState.collectAsState()
    Box(
       modifier = Modifier
           .fillMaxSize()) {

        Image(
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
            painter = painterResource(R.drawable.sign_in_image),
            contentDescription =null
        )

           BottomColumn {
               EmailTextField(
                   setEmail = {
                       loginViewModel.setEmail(it)
                   },
                   emailState = loginViewModel.emailState.collectAsState(),
                   modifier = Modifier.padding(bottom = Dimens.medium.dp)
               )
               PasswordTextField(
                   passwordState = loginViewModel.passwordState.collectAsState(),
                   setPassword = {
                        loginViewModel.setPassword(it)
                   },
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
private fun EmailTextField(
    modifier: Modifier = Modifier,
    emailState:State<String>,
    setEmail:(email:String)->Unit
) {

    val focusManager = LocalFocusManager.current

    TextField(
        maxLines = 1,
        modifier = modifier.fillMaxWidth(),
        value = emailState.value,
        shape = RoundedCornerShape(Dimens.small.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = {
            focusManager.moveFocus(FocusDirection.Down)
        }),
        onValueChange = {
          setEmail(it.trim())
        },
        placeholder = {
            TextFieldLabel(text = stringResource(R.string.screen_sign_in_email))
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = colorResource(R.color.light_grey),
            focusedIndicatorColor =  Color.Transparent, //hide the indicator,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}


@Composable
@Preview
fun PreviewPasswordField(){
    PasswordTextField(passwordState = remember {
        mutableStateOf("")
    }, setPassword = {})
}


@Composable
fun PasswordTextField(
    passwordState:State<String>,
    setPassword:(password:String)->Unit,
    modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current

        TextField(
            maxLines = 1,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            shape = RoundedCornerShape(Dimens.small.dp),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus(true)
            }),
            modifier = modifier.fillMaxWidth(),
            value = passwordState.value,
            onValueChange = {
                setPassword(it.trim())
            },
            placeholder = {
                TextFieldLabel(text = stringResource(R.string.screen_sign_in_password))
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = colorResource(R.color.light_grey),
                focusedIndicatorColor =  Color.Transparent, //hide the indicator,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

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
        shape = RoundedCornerShape(Dimens.small.dp),
        onClick = {
            login()
        }) {
         ButtonText(
             modifier = Modifier.padding(vertical = Dimens.tiny.dp),
             text = stringResource(id = R.string.screen_sign_in_login))
    }
}

@Composable
private fun RegisterButton(
    modifier: Modifier = Modifier,
    navigateToRegister: () -> Unit
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.small.dp),
        onClick = {
            navigateToRegister()
        }) {
        ButtonText(
            modifier = Modifier.padding(vertical = Dimens.tiny.dp),
            text = stringResource(id = R.string.screen_sign_in_register)
        )
    }
}



