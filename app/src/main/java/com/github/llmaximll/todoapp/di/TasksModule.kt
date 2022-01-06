package com.github.llmaximll.todoapp.di

import com.github.llmaximll.todoapp.data.tasks.TasksRepository
import com.github.llmaximll.todoapp.data.tasks.TasksRepositoryImpl
import com.github.llmaximll.todoapp.data.tasks.local.TasksLocalDataSource
import com.github.llmaximll.todoapp.data.tasks.local.TasksLocalDataSourceImpl
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