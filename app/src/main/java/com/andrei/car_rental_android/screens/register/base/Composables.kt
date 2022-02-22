package com.andrei.car_rental_android.screens.register.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.ui.Dimens

@Composable
fun RegisterScreenSurface(content : @Composable () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.surface)
        .padding(horizontal = Dimens.small.dp)){
            content()
        }
}

@Composable
internal fun ContinueButton(enabled: State<Boolean>, onClick:()->Unit){
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 100.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                onClick()
            },
            enabled = enabled.value
        ) {
            Text(text = stringResource(R.string.screen_user_name_continue))
        }
    }
}


