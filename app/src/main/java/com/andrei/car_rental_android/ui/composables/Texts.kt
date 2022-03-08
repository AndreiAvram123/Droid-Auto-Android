package com.andrei.car_rental_android.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andrei.car_rental_android.ui.Dimens

@Composable
fun ButtonText(
    text:String
){
    Text(
        fontWeight = FontWeight.Bold,
        fontSize = Dimens.medium.sp,
        modifier = Modifier.padding(vertical = Dimens.small.dp),
        text = text
    )
}