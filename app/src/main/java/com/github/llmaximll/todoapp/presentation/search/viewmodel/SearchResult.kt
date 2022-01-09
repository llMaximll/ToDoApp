package com.github.llmaximll.todoapp.presentation.search.viewmodel

import com.github.llmaximll.todoapp.domain.tasks.models.Task

sealed class SearchResult {
    object Loading : SearchResult()
    object EmptyResult : SearchResult()
    object EmptyQuery : SearchResult()
    data class SuccessResult(val result: List<Task>) : SearchResult()
    data class ErrorResult(val e: Throwable) : SearchResult()
}
