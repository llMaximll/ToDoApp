package com.github.llmaximll.todoapp.presentation.explore.viewmodel

import com.github.llmaximll.todoapp.domain.tasks.models.Category

sealed class CategoriesResult {
    object Loading : CategoriesResult()
    object EmptyResult : CategoriesResult()
    data class SuccessResult(val result: List<Category>) : CategoriesResult()
    data class ErrorResult(val e: Throwable) : CategoriesResult()
}
