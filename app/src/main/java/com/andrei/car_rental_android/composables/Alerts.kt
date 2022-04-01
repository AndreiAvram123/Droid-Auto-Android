package com.andrei.car_rental_android.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier


data class AlertDialogArgs(
    val title:String,
    val text:String,
    val confirmButtonText:String,
    val onDismiss: () -> Unit
)


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

@Composable
fun LoadingAlert(
    modifier:Modifier = Modifier
){

}
