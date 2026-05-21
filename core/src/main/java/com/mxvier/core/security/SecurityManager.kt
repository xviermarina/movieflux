package com.mxvier.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SecurityManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit { putBoolean("biometric_enabled", enabled) }
        if (enabled) {
            setBiometricRefused(false)
        }
    }

    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean("biometric_enabled", false)
    }

    fun setBiometricRefused(refused: Boolean) {
        sharedPreferences.edit { putBoolean("biometric_refused", refused) }
    }

    fun isBiometricRefused(): Boolean {
        return sharedPreferences.getBoolean("biometric_refused", false)
    }

    fun saveUserCredentials(user: String, rememberMe: Boolean) {
        sharedPreferences.edit {
            putString("saved_user", if (rememberMe) user else null)
            putBoolean("remember_me", rememberMe)
        }
    }

    fun getSavedUser(): String? {
        return sharedPreferences.getString("saved_user", null)
    }

    fun isRememberMeActive(): Boolean {
        return sharedPreferences.getBoolean("remember_me", false)
    }

    fun saveSessionToken(token: String) {
        sharedPreferences.edit { putString("session_token", token) }
    }

    fun getSessionToken(): String? {
        return sharedPreferences.getString("session_token", null)
    }

    fun logout() {
        sharedPreferences.edit { remove("session_token") }
    }
}
