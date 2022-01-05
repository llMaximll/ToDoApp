package com.github.llmaximll.todoapp.data.core.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.llmaximll.todoapp.data.explore.local.TaskEntity
import com.github.llmaximll.todoapp.data.explore.local.TasksDao
import dagger.hilt.android.qualifiers.ApplicationContext

@Database(
    entities = [
        TaskEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class TasksDb : RoomDatabase() {
    abstract fun tasksDao(): TasksDao

    companion object {
        fun create(@ApplicationContext appContext: Context): TasksDb =
            Room.databaseBuilder(
                appContext,
                TasksDb::class.java,
                "tasks_database"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}