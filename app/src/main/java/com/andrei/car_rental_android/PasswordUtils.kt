package com.andrei.car_rental_android

object PasswordUtils {

    fun String.hasLowercaseLetter() = this.contains("(?=(.*[a-z])+)".toRegex())

    fun String.hasUppercaseLetter() = this.contains("(?=(.*[A-Z])+)".toRegex())

    fun String.hasSpecialChar() = this.contains("(?=.*[!@#\$&*])".toRegex())

    fun String.hasDigit() = this.contains("(?=.*[0-9])".toRegex())

    fun String.hasMinRequiredLength() = this.length >= 8
}