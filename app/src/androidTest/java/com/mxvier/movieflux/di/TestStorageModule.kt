package com.mxvier.movieflux.di

import com.mxvier.core.di.StorageModule
import com.mxvier.core.security.SecurityManager
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [StorageModule::class]
)
abstract class TestStorageModule {

    @Binds
    @Singleton
    abstract fun bindSecurityManager(impl: FakeSecurityManager): SecurityManager
}
