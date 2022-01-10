package com.github.llmaximll.todoapp.data.tasks

import com.github.llmaximll.todoapp.data.tasks.local.TasksLocalDataSource
import com.github.llmaximll.todoapp.domain.tasks.models.Category
import com.github.llmaximll.todoapp.domain.tasks.models.Task
import com.github.llmaximll.todoapp.utils.Result
import com.github.llmaximll.todoapp.utils.mapSuccess
import javax.inject.Inject

interface TasksRepository {
    suspend fun getTask(id: Long): Result<Task, Throwable?>
    suspend fun getCategories(): Result<List<Category>, Throwable?>
    suspend fun getTasks(): Result<List<Task>, Throwable?>
    suspend fun getAllTitles(): Result<List<String>, Throwable?>
    suspend fun searchTasks(query: String): Result<List<Task>, Throwable?>
    suspend fun insertTask(task: Task): Result<Long, Throwable?>
}

class TasksRepositoryImpl @Inject constructor(
    private val tasksLocalDataSource: TasksLocalDataSource
) : TasksRepository {
    override suspend fun getTask(id: Long): Result<Task, Throwable?> {
        val task = tasksLocalDataSource.getById(id)
        val entityResult = if (task != null) {
            Result.Success(task)
        } else {
            Result.Error(task)
        }

        return entityResult.mapSuccess { entity -> entity.toModel() }
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

    override suspend fun getTasks(): Result<List<Task>, Throwable?> {
        val tasks = tasksLocalDataSource.getAll()
        val result = if (tasks != null) {
            Result.Success(tasks.map { it!!.toModel() })
        } else {
            Result.Error(null)
        }
        return result
    }

    override suspend fun getAllTitles(): Result<List<String>, Throwable?> {
        val titles = tasksLocalDataSource.getAllTitles()
        val result = if (titles != null) {
            Result.Success(titles)
        } else {
            Result.Error(null)
        }
        return result
    }

}