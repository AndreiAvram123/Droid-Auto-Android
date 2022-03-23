package com.andrei.car_rental_android.screens.register.creatingAccount

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.screens.register.base.RegisterScreenSurface
import com.andrei.car_rental_android.screens.register.creatingAccount.CreatingAccountViewModel.CreatingAccountState
import com.andrei.car_rental_android.ui.Dimens


@Composable
fun CreatingAccountScreen(
    navController: NavController
){
    RegisterScreenSurface {
        MainContent()
    }
}

@Composable
private fun MainContent(

){
    val viewModel = hiltViewModel<CreatingAccountViewModelImpl>();
    when(viewModel.creatingAccountState.collectAsState().value){
        is CreatingAccountState.Loading->{
            Animation(
                LottieCompositionSpec.RawRes(R.raw.loading),
                modifier = Modifier.fillMaxSize()
            )
        }
        is CreatingAccountState.Created->{
            SuccessContent{
                //todo
                //navigate
            }
        }
        is CreatingAccountState.Error ->{
            ErrorContent{
                viewModel.retry()
            }
        }

    }
}


@Composable
private fun CenterColumn(
     modifier:Modifier = Modifier,
     content: @Composable ()->Unit,

) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
       content()
    }
}

@Composable
private fun SuccessContent(
    navigateToLogin: () -> Unit
){
     CenterColumn {
          Animation(lottieCompositionSpec = LottieCompositionSpec.RawRes(R.raw.success))
          Text(
              text = stringResource(R.string.screen_creating_account_success),
              textAlign = TextAlign.Center,
              fontSize = Dimens.large.sp,
          )
          NavigateToLoginButton(
              modifier = Modifier
                  .padding(
                      vertical = Dimens.huge.dp,
                      horizontal = Dimens.medium.dp
                  )
                  .fillMaxWidth(),
              navigateToLogin = navigateToLogin
          )
      }
}

@Composable
private fun NavigateToLoginButton(
    modifier: Modifier = Modifier,
    navigateToLogin:()->Unit
){
    Button(
        modifier = modifier,
        onClick = {
            navigateToLogin()
        },
    ) {
        Text(text = stringResource(R.string.screen_creating_account_navigate_to_login))
    }
}


@Composable
@Preview(showBackground = true)
private fun ErrorContent(
    modifier:Modifier = Modifier,
    retryCreateAccount:()->Unit = {}
){
  CenterColumn{
       ErrorAnimation()
       Text(
           modifier = Modifier.padding(horizontal = Dimens.medium.dp),
           text = stringResource(R.string.screen_creating_account_error),
           textAlign = TextAlign.Center,
           fontSize = Dimens.medium.sp,
       )
       TryAgainButton(
           modifier = Modifier.padding(
                   vertical = Dimens.huge.dp,
                   horizontal = Dimens.large.dp
               ).fillMaxWidth(),
           onCLick = retryCreateAccount
       )
   }
}

@Composable
private fun TryAgainButton(
    modifier:Modifier = Modifier,
    onCLick:()->Unit
){
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.medium.dp),
        onClick = onCLick
    ) {
      Text(
          modifier = Modifier.padding(vertical = Dimens.small.dp),
          text = stringResource(R.string.screen_creating_account_try_again)
      )
    }
}


@Composable
private fun ErrorAnimation(
    modifier: Modifier = Modifier
){
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error))
    LottieAnimation(
        modifier = modifier,
        composition =composition ,
        iterations = LottieConstants.IterateForever,
        speed = 0.7f
    )
}

@Composable
private fun Animation(
    lottieCompositionSpec: LottieCompositionSpec,
    modifier: Modifier = Modifier
){
    val composition by rememberLottieComposition(lottieCompositionSpec)
    LottieAnimation(
        modifier = modifier,
        composition =composition ,
        iterations = LottieConstants.IterateForever
    )
}