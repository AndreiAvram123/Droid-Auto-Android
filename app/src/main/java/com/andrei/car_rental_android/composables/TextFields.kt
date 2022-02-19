package com.andrei.car_rental_android.composables

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.andrei.car_rental_android.ui.Dimens

@Composable
fun TextFieldLabel(text:String){
    Text(
        text = text,
        fontSize = Dimens.mediumTextSize
    )
}
