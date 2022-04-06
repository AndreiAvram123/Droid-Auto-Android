package com.andrei.car_rental_android

import androidx.annotation.CallSuper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@OptIn(ExperimentalCoroutinesApi::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class BaseTest {
    val dispatcher = UnconfinedTestDispatcher()


    @BeforeAll
    @CallSuper
    open fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterAll
    fun cleanUp() {
        Dispatchers.resetMain()
    }
}
