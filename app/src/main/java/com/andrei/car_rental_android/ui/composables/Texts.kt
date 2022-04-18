package com.andrei.car_rental_android.ui.composables

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.andrei.car_rental_android.ui.Dimens

@Composable
fun ButtonText(
    modifier:Modifier = Modifier,
    text:String,
    color: Color = Color.Unspecified
){
    Text(
        fontWeight = FontWeight.Bold,
        fontSize = Dimens.medium.sp,
        modifier = modifier,
        text = text,
        color = color
    )
}