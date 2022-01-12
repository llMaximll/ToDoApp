package com.github.llmaximll.todoapp.data.tasks.local

import javax.inject.Inject

interface TasksLocalDataSource {
    suspend fun getById(id: Long): TaskEntity?
    suspend fun getAll(): List<TaskEntity?>?
    suspend fun getAllTitlesAndIds(): List<TaskTitleIdEntity>?
    suspend fun searchTasks(query: String): List<TaskEntity>?
    suspend fun insertAll(tasks: List<TaskEntity>): List<Long>
    suspend fun insert(task: TaskEntity): Long
    suspend fun update(task: TaskEntity): Int
    suspend fun deleteAll()
    suspend fun delete(task: TaskEntity): Int
}

class TasksLocalDataSourceImpl @Inject constructor(
    private val tasksDao: TasksDao
) : TasksLocalDataSource {
    override suspend fun getById(id: Long): TaskEntity? = tasksDao.getById(id)

    override suspend fun getAll(): List<TaskEntity?>? =
        tasksDao.getAll()

    override suspend fun getAllTitlesAndIds(): List<TaskTitleIdEntity>? =
        tasksDao.getAllTitlesAndIds()

    override suspend fun searchTasks(query: String): List<TaskEntity>? =
        tasksDao.searchTasks(query)

    override suspend fun insertAll(tasks: List<TaskEntity>): List<Long> =
        tasksDao.insertAll(tasks)

    override suspend fun insert(task: TaskEntity): Long =
        tasksDao.insert(task)

    override suspend fun update(task: TaskEntity) =
        tasksDao.update(task)

    override suspend fun deleteAll() {
        tasksDao.deleteAll()
    }

    override suspend fun delete(task: TaskEntity) =
        tasksDao.delete(task)

}