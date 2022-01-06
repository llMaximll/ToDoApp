package com.github.llmaximll.todoapp.presentation.add.viewmodel

sealed class AddState {
    object Empty : AddState()

    sealed class InputError : AddState() {
        object Title : InputError()

        object Description : InputError()
    }

    object Success : AddState()
}
