package com.github.llmaximll.todoapp.di

import com.github.llmaximll.todoapp.data.explore.TasksRepository
import com.github.llmaximll.todoapp.data.explore.TasksRepositoryImpl
import com.github.llmaximll.todoapp.data.explore.local.TasksLocalDataSource
import com.github.llmaximll.todoapp.data.explore.local.TasksLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface TasksModule {

    @Binds
    fun bindTasksLocalDataSource(
        impl: TasksLocalDataSourceImpl
    ): TasksLocalDataSource

    @Binds
    fun bindTasksRepository(
        impl: TasksRepositoryImpl
    ): TasksRepository
}