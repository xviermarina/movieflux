package com.mxvier.auth.presentation.viewmodel

import app.cash.turbine.test
import com.mxvier.core.security.SecurityManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private val securityManager: SecurityManager = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { securityManager.isBiometricEnabled() } returns false
        every { securityManager.isBiometricRefused() } returns false
        every { securityManager.isRememberMeActive() } returns false
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = LoginViewModel(securityManager, testDispatcher)
    }

    @Test
    fun `login should emit Success when credentials are correct`() = runTest {
        every { securityManager.saveUserCredentials("admin", true) } returns Unit
        every { securityManager.saveSessionToken("mock_token") } returns Unit
        
        createViewModel()
        viewModel.login("admin", "123456", true)

        viewModel.uiState.test {
            assertEquals(LoginUiState.Initial, awaitItem())
            assertEquals(LoginUiState.Loading, awaitItem())
            advanceTimeBy(1501)
            assertEquals(LoginUiState.Success, awaitItem())
        }
    }

    @Test
    fun `login should emit Error when credentials are incorrect`() = runTest {
        createViewModel()
        viewModel.login("wrong", "password", false)

        viewModel.uiState.test {
            assertEquals(LoginUiState.Initial, awaitItem())
            assertEquals(LoginUiState.Loading, awaitItem())
            advanceTimeBy(1501)
            val error = awaitItem()
            assertTrue(error is LoginUiState.Error)
            assertEquals("Usuário ou senha incorretos.", (error as LoginUiState.Error).message)
        }
    }

    @Test
    fun `loadSavedPreferences should update state from securityManager`() {
        every { securityManager.isBiometricEnabled() } returns true
        every { securityManager.isBiometricRefused() } returns false
        every { securityManager.isRememberMeActive() } returns true
        every { securityManager.getSavedUser() } returns "savedUser"
        
        createViewModel()
        viewModel.loadSavedPreferences()

        assertTrue(viewModel.isBiometricEnabled.value)
        assertEquals("savedUser", viewModel.savedUser.value)
    }
}
