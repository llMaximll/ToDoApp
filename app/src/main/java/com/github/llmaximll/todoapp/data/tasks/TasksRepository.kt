package com.github.llmaximll.todoapp.data.tasks

import com.github.llmaximll.todoapp.data.tasks.local.TaskTitleIdEntity
import com.github.llmaximll.todoapp.data.tasks.local.TasksLocalDataSource
import com.github.llmaximll.todoapp.domain.tasks.models.Category
import com.github.llmaximll.todoapp.domain.tasks.models.Task
import com.github.llmaximll.todoapp.domain.tasks.models.TaskTitleId
import com.github.llmaximll.todoapp.utils.Result
import com.github.llmaximll.todoapp.utils.mapSuccess
import timber.log.Timber
import javax.inject.Inject

interface TasksRepository {
    suspend fun getTask(id: Long): Result<Task, Throwable?>
    suspend fun getCategories(): Result<List<Category>, Throwable?>
    suspend fun getTasks(): Result<List<Task>, Throwable?>
    suspend fun getAllTitlesAndIds(): Result<List<TaskTitleId>, Throwable?>
    suspend fun searchTasks(query: String): Result<List<Task>, Throwable?>
    suspend fun insertTask(task: Task): Result<Long, Throwable?>
    suspend fun updateTask(task: Task): Result<Int, Throwable?>
    suspend fun deleteTask(task: Task): Result<Int, Throwable?>
}

class TasksRepositoryImpl @Inject constructor(
    private val tasksLocalDataSource: TasksLocalDataSource
) : TasksRepository {
    override suspend fun getTask(id: Long): Result<Task, Throwable?> {
        val task = tasksLocalDataSource.getById(id)
        val result = if (task != null) {
            Result.Success(task.toModel())
        } else {
            Result.Error(task)
        }
        return result
    }

    override suspend fun getCategories(): Result<List<Category>, Throwable?> {
        val tasks = tasksLocalDataSource.getAll()
        val result = if (tasks != null) {
            Result.Success(tasks.toCategories())
        } else {
            Result.Error(null)
        }
        return result
    }

    override suspend fun searchTasks(query: String): Result<List<Task>, Throwable?> {
        val tasks = tasksLocalDataSource.searchTasks("$query%")
        val result = if (tasks != null) {
            Result.Success(tasks.map { it.toModel() })
        } else {
            Result.Error(null)
        }
        return result
    }

    override suspend fun insertTask(task: Task): Result<Long, Throwable?> {
        val entityTask = task.toEntity()
        val request = tasksLocalDataSource.insert(entityTask)
        val result = if (request != -1L) {
            Result.Success(request)
        } else {
            Result.Error(null)
        }
        return result
    }

    override suspend fun updateTask(task: Task): Result<Int, Throwable?> {
        val entityTask = task.toEntity()
        val request = tasksLocalDataSource.update(entityTask)
        val result = if (request != 0) {
            Result.Success(request)
        } else {
            Result.Error(null)
        }
        return result
    }

    override suspend fun getTasks(): Result<List<Task>, Throwable?> {
        val tasks = tasksLocalDataSource.getAll()
        val result = if (tasks != null) {
            Result.Success(tasks.map { it!!.toModel() })
        } else {
            Result.Error(null)
        }
        return result
    }

    override suspend fun getAllTitlesAndIds(): Result<List<TaskTitleId>, Throwable?> {
        val task = tasksLocalDataSource.getAllTitlesAndIds()
        val result = if (task != null) {
            Result.Success(task.map { it.toModel() })
        } else {
            Result.Error(null)
        }
        return result
    }

    override suspend fun deleteTask(task: Task): Result<Int, Throwable?> {
        val entityTask = task.toEntity()
        val request = tasksLocalDataSource.delete(entityTask)
        val result = if (request != 0) {
            Result.Success(request)
        } else {
            Result.Error(null)
        }
        return result
    }

}