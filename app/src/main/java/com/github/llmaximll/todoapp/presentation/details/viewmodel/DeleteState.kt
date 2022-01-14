package com.github.llmaximll.todoapp.presentation.details.viewmodel

sealed class DeleteState {

    object Initial : DeleteState()

    object Loading : DeleteState()

    object Success : DeleteState()

    object Error : DeleteState()
}
