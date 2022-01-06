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
    suspend fun insertTask(task: Task)
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

    override suspend fun insertTask(task: Task) {
        val entityTask = task.toEntity()
        tasksLocalDataSource.insert(entityTask)
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

}