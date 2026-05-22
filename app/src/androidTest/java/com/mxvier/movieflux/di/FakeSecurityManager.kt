package com.mxvier.movieflux.di

import com.mxvier.core.security.SecurityManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeSecurityManager @Inject constructor() : SecurityManager {
    private var biometricEnabled = false
    private var biometricRefused = true
    private var rememberMe = false
    private var savedUser: String? = null
    private var sessionToken: String? = "fake_token"

    override fun setBiometricEnabled(enabled: Boolean) { biometricEnabled = enabled }
    override fun isBiometricEnabled(): Boolean = biometricEnabled
    override fun setBiometricRefused(refused: Boolean) { biometricRefused = refused }
    override fun isBiometricRefused(): Boolean = biometricRefused
    override fun saveUserCredentials(user: String, rememberMe: Boolean) {
        this.savedUser = user
        this.rememberMe = rememberMe
    }
    override fun getSavedUser(): String? = savedUser
    override fun isRememberMeActive(): Boolean = rememberMe
    override fun saveSessionToken(token: String) { sessionToken = token }
    override fun getSessionToken(): String? = sessionToken
    override fun logout() { sessionToken = null }
}
