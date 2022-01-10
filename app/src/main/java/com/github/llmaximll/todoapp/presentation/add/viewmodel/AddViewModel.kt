package com.github.llmaximll.todoapp.presentation.add.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.llmaximll.todoapp.data.tasks.TasksRepository
import com.github.llmaximll.todoapp.data.tasks.local.Categories
import com.github.llmaximll.todoapp.domain.tasks.models.Task
import com.github.llmaximll.todoapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AddViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {
    private val _addState = MutableLiveData<AddState>(AddState.Loading)
    val addState: LiveData<AddState> get() = _addState

    private val _titles = MutableStateFlow<List<String>>(emptyList())

    init {
        getAllTitles()
    }

    fun add(
        title: String,
        description: String,
        category: Categories
    ) {
        val validatedTitle = Task.Title.createIfValid(title)
        val validatedDescription = Task.Description.createIfValid(description)

        when {
            (validatedTitle == null) -> {
                _addState.value = AddState.InputError.Title.Empty
            }
            (checkTitleNotUnique(validatedTitle)) -> {
                _addState.value = AddState.InputError.Title.NotUnique
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

    private fun checkTitleNotUnique(title: Task.Title): Boolean {
        val titles = _titles.value
        titles.forEach {
            if (title.value == it) {
                return true
            }
        }
        return false
    }

    private fun getAllTitles() {
        viewModelScope.launch {
            when (val result = tasksRepository.getAllTitles()) {
                is Result.Error -> Unit
                is Result.Success -> {
                    _titles.value = result.result
                }
            }
        }
    }
}