package com.github.llmaximll.todoapp.presentation.explore.viewmodel

import com.github.llmaximll.todoapp.domain.tasks.models.Task

sealed class TasksResult {
    object Loading : TasksResult()
    object EmptyResult : TasksResult()
    data class SuccessResult(val result: List<Task>) : TasksResult()
    data class ErrorResult(val e: Throwable) : TasksResult()
}
