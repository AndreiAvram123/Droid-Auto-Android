package com.andrei.car_rental_android.engine.utils

import javax.inject.Inject

class JwtUtils @Inject constructor(
    private val jwtParser: JwtParser
){
    fun isTokenValid(token:String):Boolean  {
        val decodeToken =  jwtParser.parse(token)
        return decodeToken != null && decodeToken.isNotExpired()
    }

}
