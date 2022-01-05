package com.github.llmaximll.todoapp.di

import android.content.Context
import com.github.llmaximll.todoapp.data.core.db.TasksDb
import com.github.llmaximll.todoapp.data.explore.local.TasksDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideTasksDb(
        @ApplicationContext appContext: Context
    ): TasksDb = TasksDb.create(appContext)

    @Provides
    fun provideTasksDao(tasksDb: TasksDb): TasksDao = tasksDb.tasksDao()
}