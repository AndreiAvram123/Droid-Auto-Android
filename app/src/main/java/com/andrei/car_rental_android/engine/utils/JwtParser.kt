package com.andrei.car_rental_android.engine.utils

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.util.*
import javax.inject.Inject

data class DecodedToken( @SerializedName("exp")val expiryTimestamp: Long)
fun DecodedToken.isExpired():Boolean = expiryTimestamp > System.currentTimeMillis()/1000L


interface JwtParser {

    fun parse(jwt: String): DecodedToken?
}

class JwtParserImpl @Inject constructor(
) : JwtParser {
    private val gson = Gson()
    override fun parse(jwt: String): DecodedToken? {

        if (jwt.isBlank()) {
            return null
        }

        if (jwt.count { it == '.' } != 2) {
           return null
        }

        // Pull out the payload, deliminated by `.` characters
        val tokenPayload = jwt.split('.')[1]
        val decoded = String(Base64.getDecoder().decode(tokenPayload))

        return try {
            gson.fromJson(decoded, DecodedToken::class.java);

        } catch (e: Exception) {
            null
        }
    }
}
