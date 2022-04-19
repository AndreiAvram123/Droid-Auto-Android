package com.andrei.car_rental_android

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.TestInstance

@OptIn(ExperimentalCoroutinesApi::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class BaseTest {
    protected val testScope = TestScope()
    private val  testDispatcher = StandardTestDispatcher(testScope.testScheduler)

    protected fun runDroidAutoTest(
        testBody: suspend TestScope.() -> Unit
    ) {
        Dispatchers.setMain(testDispatcher)
        // this should really be testScope.runTest() but that doesn't work
        runTest(testDispatcher, dispatchTimeoutMs = 1234, testBody = testBody)
        Dispatchers.resetMain()
    }
}
