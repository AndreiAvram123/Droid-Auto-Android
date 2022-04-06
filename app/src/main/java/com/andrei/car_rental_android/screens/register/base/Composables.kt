package com.andrei.car_rental_android.screens.register.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.ui.Dimens
import com.andrei.car_rental_android.ui.composables.ButtonText

@Composable
fun RegisterScreenSurface(
    modifier:Modifier = Modifier,
    content : @Composable () -> Unit) {
    Box(modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.surface)
        .padding(horizontal = Dimens.medium.dp)){
            content()
        }
}

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    navigateBack:()->Unit
){
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start
    ) {
      IconButton(onClick = {
          navigateBack()
      }) {
          Icon(
              modifier =Modifier.size(40.dp),
              imageVector = Icons.Filled.ChevronLeft,
              contentDescription ="Back button"
          )
      }
    }
}

@Composable
fun CenterColumn(
    modifier:Modifier = Modifier,
    content : @Composable ()->Unit
){
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        content()
    }
}
@Composable
internal fun ContinueButton(
    modifier: Modifier = Modifier,
    enabled: Boolean, onClick:()->Unit,
){
       Button(
            modifier = modifier.fillMaxWidth().padding(
                bottom = Dimens.huge.dp
            ),
            shape = RoundedCornerShape(Dimens.small.dp),
            onClick = {
                onClick()
            },
            enabled = enabled
        ) {
            ButtonText(
                modifier = Modifier.padding(vertical = Dimens.tiny.dp),
                text = stringResource(R.string.screen_names_continue)
            )
        }
    }




