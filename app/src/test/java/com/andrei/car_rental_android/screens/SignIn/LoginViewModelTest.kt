package com.andrei.car_rental_android.screens.SignIn

import app.cash.turbine.test
import com.andrei.car_rental_android.BaseViewModelTest
import com.andrei.car_rental_android.engine.repositories.LoginRepository
import com.andrei.car_rental_android.engine.request.LoginRequest
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.time.ExperimentalTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExperimentalCoroutinesApi
@ExperimentalTime
class LoginViewModelTest : BaseViewModelTest(){

    private lateinit var sut: LoginViewModel

    private val loginRepository: LoginRepository = mockk(relaxed = true)

    @BeforeAll
    fun setUpTests() {
        super.setUp()
        sut = LoginViewModelImpl(
            coroutineProvider = testScope,
            loginRepository = loginRepository
        )
    }
    @AfterEach
    fun afterEachTest(){
        sut.resetUIState()
    }
    @Test
    fun `loginUiState state flow has the default value of Default when the viewModel is created`() {
        runTest {
            sut.loginUiState.test {
                assert(awaitItem() is LoginViewModel.LoginUIState.Default)
                expectNoEvents()
            }
        }
    }
    @Test
    fun `loginUiState state flow is LoggedIn when the api call is successful `(){
        testScope.runBlockingTest {
            val loginRequest = LoginRequest(
                email = "",
                password = ""
            )
            returnSuccess {
                loginRepository.login(loginRequest)
            }

            sut.loginUiState.test {
                assert(awaitItem() is LoginViewModel.LoginUIState.Default)
                sut.login()
                assert(awaitItem() is LoginViewModel.LoginUIState.Loading)
                assert(awaitItem() is LoginViewModel.LoginUIState.LoggedIn)
                expectNoEvents()
            }
        }
    }
    @Test
    fun `loginUiState state flow is Loading while the api call is executing`(){
        testScope.runBlockingTest {
            val loginRequest = LoginRequest(
                email = "",
                password = ""
            )
            returnLoading {
                loginRepository.login(loginRequest)
            }
            sut.loginUiState.test {
                assert(awaitItem() is LoginViewModel.LoginUIState.Default)
                sut.login()
                assert(awaitItem() is LoginViewModel.LoginUIState.Loading)
                expectNoEvents()
            }
        }
    }

    @Test
    fun `loginUiState state flow is Invalid Credentials when the api returns 401`() {
        testScope.runBlockingTest {
            val loginRequest = LoginRequest(
                email = "",
                password = ""
            )
            returnFailure(errorCode = 401) {
                loginRepository.login(loginRequest)
            }
            sut.loginUiState.test {
                assert(awaitItem() is LoginViewModel.LoginUIState.Default)
                sut.login()
                assert(awaitItem() is LoginViewModel.LoginUIState.Loading)
                assert(awaitItem() is LoginViewModel.LoginUIState.InvalidCredentials)
                expectNoEvents()
            }
        }
    }
    @Test
    fun `loginUiState state flow is ServerError when the api returns connection exception`() {
        testScope.runBlockingTest {
            val loginRequest = LoginRequest(
                email = "",
                password = ""
            )
            returnConnectionException  {
                loginRepository.login(loginRequest)
            }
            sut.loginUiState.test {
                assert(awaitItem() is LoginViewModel.LoginUIState.Default)
                sut.login()
                assert(awaitItem() is LoginViewModel.LoginUIState.Loading)
                assert(awaitItem() is LoginViewModel.LoginUIState.ServerError)
                expectNoEvents()
            }
        }
    }

}