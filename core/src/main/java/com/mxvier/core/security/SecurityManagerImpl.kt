package com.mxvier.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SecurityManagerImpl @Inject constructor(
    @ApplicationContext context: Context
) : SecurityManager {
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

    override fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit { putBoolean("biometric_enabled", enabled) }
        if (enabled) {
            setBiometricRefused(false)
        }
    }

    override fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean("biometric_enabled", false)
    }

    override fun setBiometricRefused(refused: Boolean) {
        sharedPreferences.edit { putBoolean("biometric_refused", refused) }
    }

    override fun isBiometricRefused(): Boolean {
        return sharedPreferences.getBoolean("biometric_refused", false)
    }

    override fun saveUserCredentials(user: String, rememberMe: Boolean) {
        sharedPreferences.edit {
            putString("saved_user", if (rememberMe) user else null)
            putBoolean("remember_me", rememberMe)
        }
    }

    override fun getSavedUser(): String? {
        return sharedPreferences.getString("saved_user", null)
    }

    override fun isRememberMeActive(): Boolean {
        return sharedPreferences.getBoolean("remember_me", false)
    }

    override fun saveSessionToken(token: String) {
        sharedPreferences.edit { putString("session_token", token) }
    }

    override fun getSessionToken(): String? {
        return sharedPreferences.getString("session_token", null)
    }

    override fun logout() {
        sharedPreferences.edit { remove("session_token") }
    }
}
