package com.github.llmaximll.todoapp.presentation.details.viewmodel

import com.github.llmaximll.todoapp.domain.tasks.models.Task

sealed class FetchDetailsState {

    object Loading : FetchDetailsState()

    data class Result(val task: Task) : FetchDetailsState()

    data class Error(val error: Throwable) : FetchDetailsState()
}
