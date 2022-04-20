package com.andrei.car_rental_android.screens.register.Email

import app.cash.turbine.test
import com.andrei.car_rental_android.BaseViewModelTest
import com.andrei.car_rental_android.engine.repositories.RegisterRepository
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class RegisterEmailViewModelTest : BaseViewModelTest() {

    private val registerRepository:RegisterRepository = mockk(relaxed = true)



    @Test
    fun `email validation state flow should have Default value when the view model is created`() = runDroidAutoTest{
        val sut: RegisterEmailViewModel = RegisterEmailViewModelImpl(
            coroutineProvider = testScope,
            registerRepository = registerRepository
        )



        sut.emailValidationState.test {
            assert(awaitItem() is RegisterEmailViewModel.EmailValidationState.Default)
            expectNoEvents()
        }

    }


    @Test
    fun `email validation state flow should be Default when the email is empty string`() = runDroidAutoTest {
        val sut: RegisterEmailViewModel = RegisterEmailViewModelImpl(
            coroutineProvider = testScope,
            registerRepository = registerRepository
        )

        sut.emailValidationState.test {
            sut.setEmail("")
            print(awaitItem())
        }
    }


    @Test
    fun `email validation state flow should be Invalid email when the email entered is not valid `() = runDroidAutoTest {
        val sut: RegisterEmailViewModel = RegisterEmailViewModelImpl(
            coroutineProvider = testScope,
            registerRepository = registerRepository
        )

        val invalidEmail = "avramandreitiberi@gmail"
        sut.emailValidationState.test(5.seconds) {
            sut.setEmail(invalidEmail)
            assert(awaitItem() is RegisterEmailViewModel.EmailValidationState.Default)
            assert(awaitItem() is RegisterEmailViewModel.EmailValidationState.EmailValidationError.InvalidFormat)

        }
    }


    @Test
    fun `email validation state flow should be Email already taken if the email is in correct format but already taken`() = runDroidAutoTest{
        val sut: RegisterEmailViewModel = RegisterEmailViewModelImpl(
            coroutineProvider = testScope,
            registerRepository = registerRepository
        )

        val alreadyTakenEmail = "avramandreitiberi@gmail.com"

        returnError(errorCode = 406, message = RegisterRepository.errorEmailUsed){
            registerRepository.checkIfEmailIsUsed(alreadyTakenEmail)
        }
        sut.emailValidationState.test(5.seconds) {
            assert(awaitItem() is RegisterEmailViewModel.EmailValidationState.Default)
            sut.setEmail(alreadyTakenEmail)
            assert(awaitItem() is RegisterEmailViewModel.EmailValidationState.Validating)
            assert(awaitItem() is RegisterEmailViewModel.EmailValidationState.EmailValidationError.EmailAlreadyTaken)
        }

    }

    @Test
    fun `email validation state flow should be Email valid if the email is both valid in correct format and not taken`() = runDroidAutoTest {
        val sut: RegisterEmailViewModel = RegisterEmailViewModelImpl(
            coroutineProvider = testScope,
            registerRepository = registerRepository
        )

        val alreadyTakenEmail = "avramandreitiberi@gmail.com"

        returnSuccess {
            registerRepository.checkIfEmailIsUsed(alreadyTakenEmail)
        }

        sut.emailValidationState.test(5.seconds) {
            assert(awaitItem() is RegisterEmailViewModel.EmailValidationState.Default)
            sut.setEmail(alreadyTakenEmail)
            assert(awaitItem() is RegisterEmailViewModel.EmailValidationState.Validating)
            assert(awaitItem() is RegisterEmailViewModel.EmailValidationState.Valid)
        }
    }

}