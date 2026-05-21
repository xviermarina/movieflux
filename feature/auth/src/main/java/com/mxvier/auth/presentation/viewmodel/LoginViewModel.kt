package com.mxvier.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mxvier.core.security.SecurityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val securityManager: SecurityManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _savedUser = MutableStateFlow<String?>(null)
    val savedUser: StateFlow<String?> = _savedUser.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(false)
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    private val _isBiometricRefused = MutableStateFlow(false)
    val isBiometricRefused: StateFlow<Boolean> = _isBiometricRefused.asStateFlow()

    fun loadSavedPreferences() {
        _isBiometricEnabled.value = securityManager.isBiometricEnabled()
        _isBiometricRefused.value = securityManager.isBiometricRefused()

        if (securityManager.isRememberMeActive()) {
            _savedUser.value = securityManager.getSavedUser()
        }
    }

    fun login(username: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            delay(1500)

            if (username == "admin" && password == "123456") {
                securityManager.saveUserCredentials(username, rememberMe)
                securityManager.saveSessionToken("mock_token")
                _uiState.value = LoginUiState.Success
            } else {
                _uiState.value = LoginUiState.Error("Usuário ou senha incorretos.")
            }
        }
    }

    fun loginWithBiometric() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            delay(1000)
            securityManager.saveSessionToken("mock_token")
            _uiState.value = LoginUiState.Success
        }
    }

    fun enableBiometricOption(enable: Boolean) {
        securityManager.setBiometricEnabled(enable)
        _isBiometricEnabled.value = enable
        if (!enable) {
            securityManager.setBiometricRefused(true)
            _isBiometricRefused.value = true
        }
    }
    
    fun resetBiometricRefusal() {
        securityManager.setBiometricRefused(false)
        _isBiometricRefused.value = false
    }
}
