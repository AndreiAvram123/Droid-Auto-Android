package com.andrei.car_rental_android

import androidx.annotation.CallSuper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class BaseTest {
    val dispatcher = TestCoroutineDispatcher()

    val testScope = TestCoroutineScope()

    @BeforeAll
    @CallSuper
    open fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterAll
    fun cleanUp() {
        Dispatchers.resetMain()
        testScope.cleanupTestCoroutines()
        dispatcher.cleanupTestCoroutines()
    }
}
