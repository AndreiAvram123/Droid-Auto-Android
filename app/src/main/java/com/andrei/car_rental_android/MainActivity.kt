package com.andrei.car_rental_android

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.tooling.preview.Preview
import com.andrei.car_rental_android.navigation.MainNavigation
import com.andrei.car_rental_android.ui.theme.CarrentalandroidTheme
import com.andrei.car_rental_android.ui.theme.LoginBackgroundColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        setContent {
            val systemUiController  = rememberSystemUiController()
            val useDarkIcons = MaterialTheme.colors.isLight
            SideEffect {
                  systemUiController.setSystemBarsColor(
                      color = LoginBackgroundColor,
                      darkIcons = useDarkIcons
                  )
            }
            CarrentalandroidTheme {
                MainNavigation()
            }
        }
    }
}
@Composable
@Preview
fun DefaultPreview(){
    MainNavigation()
}
