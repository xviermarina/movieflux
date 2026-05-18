package com.mxvier.auth.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.content.edit

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _savedUser = MutableStateFlow<String?>(null)
    val savedUser: StateFlow<String?> = _savedUser.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(false)
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    fun loadSavedPreferences() {
        val rememberMe = sharedPreferences.getBoolean("remember_me", false)
        val biometricEnabled = sharedPreferences.getBoolean("biometric_enabled", false)

        _isBiometricEnabled.value = biometricEnabled

        if (rememberMe) {
            val user = sharedPreferences.getString("saved_user", "") ?: ""
            if (user.isNotEmpty()) {
                _savedUser.value = user
            }
        }
    }

    fun login(username: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            delay(1500)

            if (username == "admin" && password == "123456") {
                saveBasicCredentials(username, password, rememberMe)
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
            _uiState.value = LoginUiState.Success
        }
    }

    fun enableBiometricOption(enable: Boolean) {
        sharedPreferences.edit { putBoolean("biometric_enabled", enable) }
    }

    private fun saveBasicCredentials(user: String, pass: String, rememberMe: Boolean) {
        sharedPreferences.edit {
            if (rememberMe) {
                putBoolean("remember_me", true)
                putString("saved_user", user)
                putString("saved_pass", pass)
            } else {
                clear()
            }
        }
    }
}