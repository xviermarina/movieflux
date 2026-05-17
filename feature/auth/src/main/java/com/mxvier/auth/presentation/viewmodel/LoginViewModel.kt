package com.mxvier.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    fun login(user: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            delay(1500)

            if (user == "admin" && password == "123456") {
                _loginState.value = LoginUiState.Success
            } else {
                _loginState.value = LoginUiState.Error("Usuário ou senha incorretos.")
            }
        }
    }
}