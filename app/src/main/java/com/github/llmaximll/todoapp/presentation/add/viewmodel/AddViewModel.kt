package com.github.llmaximll.todoapp.presentation.add.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.llmaximll.todoapp.data.tasks.TasksRepository
import com.github.llmaximll.todoapp.data.tasks.local.Categories
import com.github.llmaximll.todoapp.domain.tasks.models.Task
import com.github.llmaximll.todoapp.domain.tasks.models.TaskTitleId
import com.github.llmaximll.todoapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AddViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {
    private val _addState = MutableLiveData<AddState>(AddState.Initial)
    val addState: LiveData<AddState> get() = _addState

    private val _titles = MutableStateFlow<List<TaskTitleId>>(emptyList())

    var date: Calendar = Calendar.getInstance()

    init {
        getAllTitlesAndIds()
        date.add(Calendar.DAY_OF_MONTH, 1)
    }

    fun add(
        title: String,
        description: String,
        category: Categories,
        date: Long = this.date.timeInMillis
    ) {
        _addState.value = AddState.Loading

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
                executeAdd(title, description, category, date)
            }
        }
    }

    private fun executeAdd(title: String, description: String, category: Categories, date: Long) {
        val task = Task(
            id = Random.nextLong(),
            title = Task.Title(title),
            description = Task.Description(description),
            category = category,
            done = false,
            date = date
        )
        Timber.i("id=${task.id}")
        viewModelScope.launch {
            handleAdd(task)
        }
    }

    private fun checkTitleNotUnique(title: Task.Title): Boolean {
        val titles = _titles.value
        titles.forEach {
            if (title.value == it.title.value) {
                return true
            }
        }
        return false
    }

    private fun getAllTitlesAndIds() {
        viewModelScope.launch {
            when (val result = tasksRepository.getAllTitlesAndIds()) {
                is Result.Error -> Unit
                is Result.Success -> {
                    _titles.value = result.result
                }
            }
        }
    }

    private suspend fun handleAdd(task: Task) {
        when (val result = tasksRepository.insertTask(task)) {
            is Result.Error -> _addState.value = AddState.Error
            is Result.Success -> {
                Timber.i("Add result=$result")
                _addState.value = AddState.Success(task.id)
            }
        }
    }
}