package com.andrei.car_rental_android.screens.SignIn

import app.cash.turbine.test
import com.andrei.car_rental_android.BaseViewModelTest
import com.andrei.car_rental_android.engine.repositories.LoginRepository
import com.andrei.car_rental_android.engine.request.LoginRequest
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.Random::class)
@OptIn(ExperimentalCoroutinesApi::class)
    class LoginViewModelTest : BaseViewModelTest(){


    private val loginRepository: LoginRepository = mockk(relaxed = true)
    private val loginRequest = LoginRequest(
        email = "andrei@gmail.com",
        password = "andrei1239"
    )


    @Test
        fun `loginUiState state flow has the default value of Default when the viewModel is created`() = runTest {
        val sut: LoginViewModel  = LoginViewModelImpl(
            coroutineProvider = this,
            loginRepository = loginRepository
        )
        sut.loginUiState.test {
            assert(awaitItem() is LoginViewModel.LoginUIState.Default)
            expectNoEvents()
        }
    }

    @Test
    fun `loginUiState state flow is LoggedIn when the api call is successful `() = runTest {
        val sut: LoginViewModel  = LoginViewModelImpl(
            coroutineProvider = this,
            loginRepository = loginRepository
        )
        sut.setEmail(loginRequest.email)
        sut.setPassword(loginRequest.password)
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
    @Test
    fun `loginUiState state flow is Loading while the api call is executing`() = runTest {

        val sut: LoginViewModel  = LoginViewModelImpl(
            coroutineProvider = this,
            loginRepository = loginRepository
        )
        sut.setEmail(loginRequest.email)
        sut.setPassword(loginRequest.password)

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

    @Test
    fun `loginUiState state flow is Invalid Credentials when the api returns 401`() = runTest {

        val sut: LoginViewModel  = LoginViewModelImpl(
            coroutineProvider = this,
            loginRepository = loginRepository
        )
        sut.setEmail(loginRequest.email)
        sut.setPassword(loginRequest.password)

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
    @Test
    fun `loginUiState state flow is ServerError when the api returns connection exception`() = runTest {
        val sut: LoginViewModel  = LoginViewModelImpl(
            coroutineProvider = this,
            loginRepository = loginRepository
        )
        sut.setEmail(loginRequest.email)
        sut.setPassword(loginRequest.password)

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
