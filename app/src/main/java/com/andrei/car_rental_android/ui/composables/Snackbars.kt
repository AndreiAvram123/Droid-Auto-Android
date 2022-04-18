package com.andrei.car_rental_android.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andrei.car_rental_android.R
import com.andrei.car_rental_android.ui.Dimens


@Composable
private fun LoadingSnackbarPreview(){
    LoadingSnackbar(
        text = stringResource(id = R.string.screen_home_loading_location)
    )

}

@Composable
private fun SnackbarLayout(
    modifier:Modifier = Modifier,
    content:@Composable () -> Unit){
    Snackbar(
        modifier = modifier.padding(horizontal = Dimens.small.dp),
        shape = RoundedCornerShape(Dimens.small.dp)
    ){
        content()
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSnackbars(){
    Column {
        CustomSnackbar(text = "We could not determine your location")
        Spacer(modifier = Modifier.height(Dimens.large.dp))
        LoadingSnackbar(text = "We are trying to load your location")
    }
}

@Composable
fun CustomSnackbar(
    modifier: Modifier = Modifier,
    text:String
){
    SnackbarLayout {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .padding(Dimens.small.dp),
            text = text,
            fontWeight = FontWeight.SemiBold,
            fontSize = Dimens.medium.sp,
            textAlign = TextAlign.Center
        )

    }
}

@Composable
fun LoadingSnackbar(
    modifier: Modifier = Modifier,
    text:String
) {
    SnackbarLayout(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.small.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.width(Dimens.medium.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = text,
                fontWeight = FontWeight.SemiBold,
                fontSize = Dimens.medium.sp
            )
        }
    }
}

