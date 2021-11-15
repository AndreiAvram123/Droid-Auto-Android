package com.andrei.car_rental_android.screens.register.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andrei.car_rental_android.ui.Dimens

@Composable
fun RegisterScreenSurface(content : @Composable () -> Unit) {
    Box(modifier = Modifier
        .background(MaterialTheme.colors.surface)
        .padding(horizontal = Dimens.medium.dp)){
            content()
        }
}


