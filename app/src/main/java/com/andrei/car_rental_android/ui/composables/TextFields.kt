package com.andrei.car_rental_android.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andrei.car_rental_android.ui.Dimens


@Composable
fun TextFieldErrorMessage(errorMessage:String){
    Text(
        text = errorMessage,
        color = MaterialTheme.colors.error,
        style = MaterialTheme.typography.caption,
        modifier = Modifier.padding(start = Dimens.small.dp)
    )
}