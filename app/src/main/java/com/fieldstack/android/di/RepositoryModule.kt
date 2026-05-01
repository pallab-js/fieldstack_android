package com.fieldstack.android.di

import com.fieldstack.android.data.repository.FakeFieldStackRepository
import com.fieldstack.android.data.repository.FieldStackRepository
import com.fieldstack.android.data.repository.RealFieldStackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // Switch to RealFieldStackRepository for prod builds by changing the binding below.
    // For Phase 1 (dev flavor) we keep the fake; Phase 2 wires the real impl.
    @Binds @Singleton
    abstract fun bindRepository(impl: FakeFieldStackRepository): FieldStackRepository
}
