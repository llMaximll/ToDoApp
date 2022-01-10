package com.github.llmaximll.todoapp.data.tasks.local

import javax.inject.Inject

interface TasksLocalDataSource {
    suspend fun getById(id: Long): TaskEntity?
    suspend fun getAll(): List<TaskEntity?>?
    suspend fun getAllTitles(): List<String>?
    suspend fun searchTasks(query: String): List<TaskEntity>?
    suspend fun insertAll(tasks: List<TaskEntity>): List<Long>
    suspend fun insert(task: TaskEntity): Long
    suspend fun deleteAll()
    suspend fun delete(id: Long)
}

class TasksLocalDataSourceImpl @Inject constructor(
    private val tasksDao: TasksDao
) : TasksLocalDataSource {
    override suspend fun getById(id: Long): TaskEntity? = tasksDao.getById(id)

    override suspend fun getAll(): List<TaskEntity?>? =
        tasksDao.getAll()

    override suspend fun getAllTitles(): List<String>? =
        tasksDao.getAllTitles()

    override suspend fun searchTasks(query: String): List<TaskEntity>? =
        tasksDao.searchTasks(query)

    override suspend fun insertAll(tasks: List<TaskEntity>): List<Long> =
        tasksDao.insertAll(tasks)

    override suspend fun insert(task: TaskEntity): Long =
        tasksDao.insert(task)

    override suspend fun deleteAll() {
        tasksDao.deleteAll()
    }

    override suspend fun delete(id: Long) {
        tasksDao.delete(id)
    }

}