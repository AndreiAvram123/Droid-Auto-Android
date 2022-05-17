package com.andrei.car_rental_android.screens.Settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.screens.register.base.BackButton
import com.andrei.car_rental_android.ui.Dimens
import com.andrei.car_rental_android.ui.composables.ButtonText


@Composable
fun SettingsScreen(
    navController: NavController
) {
    val viewModel: SettingsViewModel = hiltViewModel<SettingsViewModelImpl>()
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Content(
            viewModel = viewModel,
            navController = navController
        )
    }
}



@Composable
private fun Content(
    viewModel:SettingsViewModel,
    navController: NavController
){
  Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.SpaceBetween
  ) {
      BackButton {
          navController.popBackStack()
      }
      SignOutButton(signOut = {
          viewModel.signOut()
      })
  }

}


@Composable
@Preview
private fun SignOutButton(
    modifier: Modifier = Modifier,
    signOut:()->Unit = {}
){

    Button(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.medium.dp),
        shape = RoundedCornerShape(Dimens.small.dp),
        onClick =signOut,
    ) {
        ButtonText(
            modifier = Modifier.padding(vertical = Dimens.tiny.dp),
            text = stringResource(R.string.screen_settings_sign_out)
        )
    }
}