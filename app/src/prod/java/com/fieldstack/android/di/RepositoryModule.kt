package com.fieldstack.android.di

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
    @Binds @Singleton
    abstract fun bindRepository(impl: RealFieldStackRepository): FieldStackRepository
}
