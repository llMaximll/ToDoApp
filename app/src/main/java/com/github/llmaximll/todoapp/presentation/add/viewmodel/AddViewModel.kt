package com.github.llmaximll.todoapp.presentation.add.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.llmaximll.todoapp.data.tasks.TasksRepository
import com.github.llmaximll.todoapp.data.tasks.local.Categories
import com.github.llmaximll.todoapp.domain.tasks.models.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AddViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {
    private val _addState = MutableLiveData<AddState>(AddState.Empty)

    val addState: LiveData<AddState> get() = _addState

    fun add(
        title: String,
        description: String,
        category: Categories
    ) {
        val validatedTitle = Task.Title.createIfValid(title)
        val validatedDescription = Task.Description.createIfValid(description)

        when {
            (validatedTitle == null) -> {
                _addState.value = AddState.InputError.Title
            }
            (validatedDescription == null) -> {
                _addState.value = AddState.InputError.Description
            }
            else -> {
                executeAdd(title, description, category)
            }
        }
    }

    private fun executeAdd(title: String, description: String, category: Categories) {
        val task = Task(
            id = Random.nextLong(),
            title = Task.Title(title),
            description = Task.Description(description),
            category = category,
            done = false
        )
        viewModelScope.launch {
            tasksRepository.insertTask(task)
            _addState.value = AddState.Success
        }
    }
}