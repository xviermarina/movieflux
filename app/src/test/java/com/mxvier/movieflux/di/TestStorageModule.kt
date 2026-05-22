package com.mxvier.movieflux.di

import android.content.Context
import android.content.SharedPreferences
import com.mxvier.core.di.StorageModule
import com.mxvier.core.security.SecurityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [StorageModule::class]
)
object TestStorageModule {

    @Provides
    @Singleton
    fun provideSecurityManager(): SecurityManager {
        return FakeSecurityManager()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        // Use standard SharedPreferences for Robolectric to avoid KeyStore issues
        return context.getSharedPreferences("test_prefs", Context.MODE_PRIVATE)
    }
}
