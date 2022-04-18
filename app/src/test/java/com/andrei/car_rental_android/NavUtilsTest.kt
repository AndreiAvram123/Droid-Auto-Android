package com.andrei.car_rental_android

import com.andrei.car_rental_android.ui.utils.replaceArgumentValues
import org.junit.jupiter.api.Test

class NavUtilsTest {

    @Test
    fun `Given navigation destination applied function should give route `(){
        val destination = "something?carID=32342d"
        destination.replaceArgumentValues()
        print("Destination "  + destination.replaceArgumentValues())
    }
}