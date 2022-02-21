package com.andrei.car_rental_android

object PasswordUtils {

    fun String.hasLowercaseLetter() = this.contains("(?=(.*[a-z])+)".toRegex())

    fun String.hasUppercaseLetter() = this.contains("(?=(.*[A-Z])+)".toRegex())
}