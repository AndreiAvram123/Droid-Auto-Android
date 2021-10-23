package com.andrei.car_rental_android

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andrei.car_rental_android.screens.SignIn.SignInScreen
import com.andrei.car_rental_android.ui.Dimens
import com.andrei.car_rental_android.ui.theme.CarrentalandroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignInScreen()
        }
    }
}

@Composable
fun ScreenContent(names: List<String>){
    Column {
        names.forEach{
            Greeting(name = it)
            Divider()
        }
    }
}
@Composable
fun Counter(count: Int, updateCount:(Int)-> Unit) {
    Button(onClick = { updateCount(count +1) }) {
        Text(text = "I've been clicked $count times")
    }
}

@Composable
@Preview(showBackground = true)
fun MainActivityUI() {
    CarrentalandroidTheme {
        var counterState by remember {
            mutableStateOf(0)
        }
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            Column(modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()) {
                namesList(names = List(1000) {
                    "Hello Android $it"
                })
//              Column(modifier = Modifier.weight(1f)) {
//                  namesList(names = listOf("Android , IOS"))
//                  Counter(count = counterState, updateCount = {
//                      counterState ++
//                  }
//                )
//              }
//             if(counterState > 2){
//                 Text(text = "You pressed this several times")
//             }
//            }

                
            }
        }
    }
}

@Composable
fun namesList(names:List<String>){
   LazyColumn(modifier = Modifier.fillMaxWidth()){
      items(items = names){
          //the corresponding UI
         Greeting(name = it)
      }
   }
}

@Composable
fun Greeting(name: String) {
    var isSelected by remember {
        mutableStateOf(false)
    }
    //the Greeting function will run again once it's state changes and this will be triggered
    //generate the intermediate colors necessary to make the animation nice
    val targetColor  by animateColorAsState(
        targetValue = if(isSelected) MaterialTheme.colors.primary else Color.Transparent,
        animationSpec = tween(durationMillis = 4000)
    )
    Surface(color = targetColor) {
        Text(
            text = "Hello $name!",
            color = Color.Blue,
            modifier = Modifier
                .clickable {
                    isSelected = !isSelected
                }
                .padding(8.dp)

        )
    }
}
@Composable
@Preview
fun DefaultPreview(){
    MainActivityUI()
}
