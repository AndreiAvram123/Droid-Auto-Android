package com.andrei.car_rental_android.screens.SignIn

import android.inputmethodservice.Keyboard
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.ui.Dimens

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
fun FullBackgroundImage(@DrawableRes imageRes:Int){
    Image(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        contentScale = ContentScale.FillBounds,
        painter = painterResource(imageRes),
        contentDescription = null)
}


@Composable
fun MainColumn(){
    val loginViewModel = hiltViewModel<LoginViewModelImpl>()
    Box {
        //FullBackgroundImage(imageRes = R.drawable.mock_background)
        BottomedCenteredColumn {
            UsernameTextField(
                viewModel = loginViewModel,
                modifier = Modifier.padding(bottom = Dimens.medium.dp)
            )
            PasswordTextField(
                viewModel = loginViewModel,
                modifier = Modifier.padding(bottom = Dimens.large.dp)
            )
            SignInLayout(loginViewModel = loginViewModel)
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

@Preview
@Composable
fun defaultPreview(){
   MainUI()
}


@Composable
fun UsernameTextField(viewModel: LoginViewModel, modifier: Modifier = Modifier){
    val usernameState = viewModel.usernameState.collectAsState()

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        shape = RoundedCornerShape(Dimens.medium.dp),
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

    OutlinedTextField(
        shape = RoundedCornerShape(Dimens.medium.dp),
        maxLines = 1,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions (onDone = {
           focusManager.clearFocus(true)
        }),
        modifier = modifier.fillMaxWidth(),
        value = passwordState.value,
        onValueChange ={
            viewModel.setPassword(it)
        },
        label = {
            Text(text = stringResource(R.string.screen_sign_in_password))
        }
    )
}
@Composable
fun SignInLayout(loginViewModel: LoginViewModel){
    val loginState = loginViewModel.loginUiState.collectAsState()
    val context = LocalContext.current
    when(val loginStateValue = loginState.value){
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
            Toast.makeText(context,"Invalid credentials ",Toast.LENGTH_SHORT).show()
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
        onClick = {
            viewModel.login()
        }) {
        Text(
            modifier = Modifier.padding(vertical = Dimens.small.dp),
            text = stringResource(R.string.screen_sign_in_login)
        )
    }
}


