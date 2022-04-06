package com.andrei.car_rental_android.screens.Home.useCases

import javax.inject.Inject
import kotlin.time.Duration

class FormatTimeUseCase @Inject constructor() {
    operator fun invoke(duration:Duration):String{
        val seconds = duration.inWholeSeconds
        val formattedSeconds:String = if(seconds % 60 < 10){
            //add a zero to the beginning
            "0${seconds % 60}"
        }else{
            (seconds % 60).toString()
        }
        return "${duration.inWholeMinutes}:$formattedSeconds"
    }
}