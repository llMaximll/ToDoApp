package com.github.llmaximll.todoapp.presentation.add.viewmodel

import com.github.llmaximll.todoapp.domain.tasks.models.Task

sealed class AddState {
    object Initial : AddState()

    object Loading : AddState()

    sealed class InputError : AddState() {

        sealed class Title : InputError() {

            object Empty : Title()

            object NotUnique : Title()
        }

        object Description : InputError()
    }

    object Error : AddState()

    data class Success(val taskId: Long) : AddState()
}
