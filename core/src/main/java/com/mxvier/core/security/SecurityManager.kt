package com.mxvier.core.security

interface SecurityManager {
    fun setBiometricEnabled(enabled: Boolean)
    fun isBiometricEnabled(): Boolean
    fun setBiometricRefused(refused: Boolean)
    fun isBiometricRefused(): Boolean
    fun saveUserCredentials(user: String, rememberMe: Boolean)
    fun getSavedUser(): String?
    fun isRememberMeActive(): Boolean
    fun saveSessionToken(token: String)
    fun getSessionToken(): String?
    fun logout()
}
