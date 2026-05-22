package com.mxvier.movieflux.di

import com.mxvier.core.security.SecurityManager

class FakeSecurityManager : SecurityManager {
    override fun setBiometricEnabled(enabled: Boolean) {}
    override fun isBiometricEnabled(): Boolean = false
    override fun setBiometricRefused(refused: Boolean) {}
    override fun isBiometricRefused(): Boolean = true
    override fun saveUserCredentials(user: String, rememberMe: Boolean) {}
    override fun getSavedUser(): String? = null
    override fun isRememberMeActive(): Boolean = false
    override fun saveSessionToken(token: String) {}
    override fun getSessionToken(): String? = "fake_token"
    override fun logout() {}
}
