package com.andrei.car_rental_android.screens.register.base

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.ui.Dimens
import com.andrei.car_rental_android.ui.composables.ButtonText



@Composable
@Preview
private fun PreviewOutlinedButton(){
    CustomOutlinedButton(
        modifier = Modifier,
        onClick = {},
        text = "Lock",
        imageVector = Icons.Filled.Lock
    )
}

@Preview
@Composable
private fun PreviewCustomButton(){
    CustomButton(text = "End") {

    }
}

@Composable
fun CustomOutlinedButton(
    modifier:Modifier = Modifier,
    text:String,
    imageVector:ImageVector? = null,
    onClick: () -> Unit
){
    OutlinedButton(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.medium.dp),
        onClick = onClick,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colors.primary
        )
    ) {
        ButtonContentLayout{
            if(imageVector != null){
                Icon(
                    modifier = Modifier.fillMaxHeight(),
                    imageVector = imageVector ,
                    contentDescription = null
                )
            }
            ButtonText(text = text)
        }
    }
}


@Composable
private fun ButtonContentLayout(content: @Composable () -> Unit){
    Row(
        modifier = Modifier
            .padding(
                horizontal = Dimens.extraSmall.dp,
                vertical = Dimens.small.dp
            ).height(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.medium.dp)
    ){
        content()
    }
}
@Composable
fun CustomButton(
    modifier :Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.medium.dp)
    ) {
        ButtonContentLayout{
            ButtonText(
                text = text,
                color = MaterialTheme.colors.onPrimary
            )
        }
    }
}

    @Composable
    fun RegisterScreenSurface(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
    ) {
        Box(
            modifier = modifier
                .background(MaterialTheme.colors.surface)
                .padding(horizontal = Dimens.medium.dp)
        ) {
            content()
        }
    }

    @Composable
    fun BackButton(
        modifier: Modifier = Modifier,
        navigateBack: () -> Unit
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = {
                navigateBack()
            }) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "Back button"
                )
            }
        }
    }
    @Composable
    fun ContinueButton(
        modifier: Modifier = Modifier,
        enabled: Boolean, onClick: () -> Unit,
    ) {
        Button(
            modifier = modifier
                .fillMaxWidth()
                .padding(
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







