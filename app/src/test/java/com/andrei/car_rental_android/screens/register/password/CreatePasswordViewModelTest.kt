package com.andrei.car_rental_android.screens.register.password

import app.cash.turbine.test
import com.andrei.car_rental_android.BaseViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class CreatePasswordViewModelTest : BaseViewModelTest(){


    //this test also applies when the view model is created, as the password is empty
    @Test
    fun `Given empty password the strength of the password does not meet any requirements`() = runDroidAutoTest{
        val sut = CreatePasswordViewModelImpl(
            coroutineProvider = testScope,
        )
        val password = ""
        sut.setPassword(password)
        sut.passwordStrength.test {
            assert(awaitItem().isEmpty())
            expectNoEvents()
        }
    }

    @Test
    fun `Given password with lowercase letter, the password should only meet the lowercase letter requirement`() = runDroidAutoTest {
        val sut = CreatePasswordViewModelImpl(
            coroutineProvider = testScope,
        )
        val password = "a"
        val expectedRequirements = listOf(
            CreatePasswordViewModel.PasswordRequirement.IncludesLowercaseLetter
        )
        sut.passwordStrength.test {
            //default
            awaitItem()
            sut.setPassword(password)
            val requirementsMet = awaitItem()

            assert(requirementsMet == expectedRequirements)
            expectNoEvents()
        }
    }

    @Test
    fun `Given password with lowercase letter and uppercase letter, the password should meet these two requirements`() = runDroidAutoTest {
        val sut = CreatePasswordViewModelImpl(
            coroutineProvider = testScope,
        )
        val password = "aA"
        sut.passwordStrength.test {
            //default
            awaitItem()

            sut.setPassword(password)
            val requirementsMet = awaitItem()
            val expectedRequirements = listOf(
                CreatePasswordViewModel.PasswordRequirement.IncludesLowercaseLetter,
                CreatePasswordViewModel.PasswordRequirement.IncludesUppercaseLetter,
            )
            assert(requirementsMet == expectedRequirements)
            expectNoEvents()
        }
    }
    @Test
    fun `Given password with lowercase letter ,uppercase letter, and number the password should meet these requirements`() = runDroidAutoTest {
        val sut = CreatePasswordViewModelImpl(
            coroutineProvider = testScope,
        )
        val password = "aA9"
        sut.passwordStrength.test {
            //default
            awaitItem()

            sut.setPassword(password)
            val requirementsMet = awaitItem()
            val expectedRequirements = listOf(
                CreatePasswordViewModel.PasswordRequirement.IncludesLowercaseLetter,
                CreatePasswordViewModel.PasswordRequirement.IncludesUppercaseLetter,
                CreatePasswordViewModel.PasswordRequirement.IncludesNumber,
            )
            assert(requirementsMet == expectedRequirements)
            expectNoEvents()
        }
    }
    @Test
    fun `Given password with lowercase letter ,uppercase letter, number and special character ,the password should meet these requirements`() = runDroidAutoTest {
        val sut = CreatePasswordViewModelImpl(
            coroutineProvider = testScope,
        )
        val password = "aA9!"
        sut.passwordStrength.test {
            //default
            awaitItem()

            sut.setPassword(password)
            val requirementsMet = awaitItem()
            val expectedRequirements = listOf(
                CreatePasswordViewModel.PasswordRequirement.IncludesLowercaseLetter,
                CreatePasswordViewModel.PasswordRequirement.IncludesUppercaseLetter,
                CreatePasswordViewModel.PasswordRequirement.IncludesNumber,
                CreatePasswordViewModel.PasswordRequirement.IncludesSpecialCharacter,
            )
            assert(requirementsMet == expectedRequirements)
            expectNoEvents()
        }
    }
    @Test
    fun `Given password with lowercase ,uppercase letter , digit , special character and min number of characters  ,the password should meet all requirements`() = runDroidAutoTest {
        val sut = CreatePasswordViewModelImpl(
            coroutineProvider = testScope,
        )
        val password = "aA9!sdfsdfsdf"
        sut.passwordStrength.test {
            //default
            awaitItem()

            sut.setPassword(password)
            val requirementsMet = awaitItem()
            val expectedRequirements = listOf(
                CreatePasswordViewModel.PasswordRequirement.IncludesLowercaseLetter,
                CreatePasswordViewModel.PasswordRequirement.IncludesUppercaseLetter,
                CreatePasswordViewModel.PasswordRequirement.IncludesNumber,
                CreatePasswordViewModel.PasswordRequirement.IncludesSpecialCharacter,
                CreatePasswordViewModel.PasswordRequirement.IncludesMinNumberCharacters,
            )
            assert(requirementsMet == expectedRequirements)
            expectNoEvents()
        }
    }

    @Test
    fun `Given valid password and reentered password that don't match , the reentered password validation state flow contains the value INVALID`() = runDroidAutoTest{
        val sut = CreatePasswordViewModelImpl(
            coroutineProvider = testScope,
        )
        val password = "aA9!sdfsdfsdf"
        val renteredPassword = "aA9!sdfsddfgdf"
        sut.reenteredPasswordValidation.test {
            sut.setPassword(password)
            sut.setReenteredPassword(renteredPassword)
            assert(awaitItem() is CreatePasswordViewModel.ReenteredPasswordValidation.NotValidated)
            assert(awaitItem() is CreatePasswordViewModel.ReenteredPasswordValidation.Invalid)
        }
    }
    @Test
    fun `Given valid password and reentered password that  match , the reentered password validation flow contains VALID`() = runDroidAutoTest{
        val sut = CreatePasswordViewModelImpl(
            coroutineProvider = testScope,
        )
        val password = "aA9!sdfsdfsdf"
        val reenteredPassword = "aA9!sdfsdfsdf"
        sut.reenteredPasswordValidation.test {
            sut.setPassword(password)
            sut.setReenteredPassword(reenteredPassword)
            assert(awaitItem() is CreatePasswordViewModel.ReenteredPasswordValidation.NotValidated)
            assert(awaitItem() is CreatePasswordViewModel.ReenteredPasswordValidation.Invalid)
        }
    }
}