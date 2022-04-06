package com.andrei.car_rental_android.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.ui.Dimens


data class AlertDialogArgs(
    val title:String,
    val text:String,
    val confirmButtonText:String,
    val onDismiss: () -> Unit
)

@Preview
@Composable
fun LoadingAlert(
    text: String? = null
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.semi_transparent)),
        contentAlignment = Alignment.Center,
    ) {
       Column(
            modifier = Modifier.size(150.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
           CircularProgressIndicator(
               modifier = Modifier.size(70.dp)
           )
           if (text != null) {
               Text(
                   modifier = Modifier.padding(top = Dimens.medium.dp),
                   text = text,
                   color = Color.Black,
                   fontSize = Dimens.large.sp,
                   fontWeight = FontWeight.SemiBold
               )
           }
       }
    }
}


@Composable
fun CarRentalDialog(
    dialogOpened: State<Boolean>,
    alertDialogArgs: AlertDialogArgs

) {
    if (dialogOpened.value) {
        AlertDialog(
            backgroundColor = MaterialTheme.colors.surface,
            onDismissRequest = alertDialogArgs.onDismiss,
            title = {
                Text(
                    text = alertDialogArgs.title,
                    color = MaterialTheme.colors.onSurface
                )
            },
            text = {
                Text(
                    text = alertDialogArgs.text,
                    color = MaterialTheme.colors.onSurface
                )
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = alertDialogArgs.onDismiss) {
                        Text(
                            text = alertDialogArgs.confirmButtonText
                        )
                    }
                }
            }
        )
    }
}