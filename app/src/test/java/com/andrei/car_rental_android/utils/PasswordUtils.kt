package com.andrei.car_rental_android.utils

import com.andrei.car_rental_android.PasswordUtils.hasLowercaseLetter
import com.andrei.car_rental_android.PasswordUtils.hasUppercaseLetter
import org.junit.jupiter.api.Test

class PasswordUtilsTest {

    @Test
    fun `if password contains lowercase letter method should return true`(){
        val password= "AAdAAA"
        assert(password.hasLowercaseLetter())
    }
    @Test
    fun `if password does not contain lowercase letter method should return false`(){
        val password= "AAAAAA"
        assert(!password.hasLowercaseLetter())
    }
    @Test
    fun `if password contains uppercase letter method should return true`(){
        val password = "aAAAA"
        assert(password.hasUppercaseLetter())
    }
    @Test
    fun `if password does not contain uppercase letter method should return false`(){
        val password = "aaaaa"
        assert(!password.hasUppercaseLetter())
    }

}