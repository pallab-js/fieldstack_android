package com.fieldstack.android.di

import android.content.Context
import androidx.room.Room
import com.fieldstack.android.data.local.FieldStackDatabase
import com.fieldstack.android.data.local.ReportDao
import com.fieldstack.android.data.local.SyncQueueDao
import com.fieldstack.android.data.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): FieldStackDatabase =
        Room.databaseBuilder(ctx, FieldStackDatabase::class.java, "fieldstack.db")
            .addMigrations(FieldStackDatabase.MIGRATION_2_3, FieldStackDatabase.MIGRATION_3_4)
            .build()

    @Provides fun provideTaskDao(db: FieldStackDatabase): TaskDao = db.taskDao()
    @Provides fun provideReportDao(db: FieldStackDatabase): ReportDao = db.reportDao()
    @Provides fun provideSyncQueueDao(db: FieldStackDatabase): SyncQueueDao = db.syncQueueDao()
    @Provides fun provideCommentDao(db: FieldStackDatabase): CommentDao = db.commentDao()
}
