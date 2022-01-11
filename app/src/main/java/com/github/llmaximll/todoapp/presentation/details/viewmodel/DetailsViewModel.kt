package com.github.llmaximll.todoapp.presentation.details.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
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
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {
    var taskId: Long = -1L
    private var initialTask: Task? = null

    private val _state = MutableStateFlow<FetchDetailsState>(FetchDetailsState.Loading)
    val state: LiveData<FetchDetailsState>
        get() = _state
            .asLiveData(viewModelScope.coroutineContext)

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Initial)
    val updateState: LiveData<UpdateState>
        get() = _updateState
            .asLiveData(viewModelScope.coroutineContext)

    private val _titles = MutableStateFlow<List<TaskTitleId>>(emptyList())

    init {
        getAllTitlesAndIds()
    }

    fun fetchData() {
        viewModelScope.launch {
            _state.value = handleTask()
        }
    }

    private suspend fun handleTask(): FetchDetailsState {
        return when (val result = tasksRepository.getTask(taskId)) {
            is Result.Error -> FetchDetailsState.Error(IllegalArgumentException("Task not found"))
            is Result.Success -> {
                initialTask = result.result
                FetchDetailsState.Result(result.result)
            }
        }
    }

    fun update(
        title: String,
        description: String,
        category: Categories,
        done: Boolean
    ) {
        _updateState.value = UpdateState.Loading

        val validatedTitle = Task.Title.createIfValid(title)
        val validatedDescription = Task.Description.createIfValid(description)

        when {
            (initialTask?.isEqualTask(
                Task.Title(title), Task.Description(description), category, done
            ) == true) -> {
                _updateState.value = UpdateState.Success
            }
            (validatedTitle == null) -> {
                _updateState.value = UpdateState.InputError.Title.Empty
            }
            (checkTitleNotUnique(validatedTitle)) -> {
                _updateState.value = UpdateState.InputError.Title.NotUnique
            }
            (validatedDescription == null) -> {
                _updateState.value = UpdateState.InputError.Description
            }
            else -> {
                executeUpdate(title, description, category)
            }
        }
    }

    private fun executeUpdate(title: String, description: String, category: Categories) {
        val task = Task(
            id = taskId,
            title = Task.Title(title),
            description = Task.Description(description),
            category = category,
            done = false
        )
        viewModelScope.launch {
            handleUpdate(task)
        }
    }

    private fun checkTitleNotUnique(title: Task.Title): Boolean {
        if (_titles.value.isEmpty()) return true
        val titles = _titles.value
        titles.forEach {
            if (title.value == it.title.value && initialTask?.id != it.id) {
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

    private suspend fun handleUpdate(task: Task) {
        Timber.i("new task=$task")
        when (val result = tasksRepository.updateTask(task)) {
            is Result.Error -> _updateState.value = UpdateState.Error
            is Result.Success -> {
                Timber.i("Update result=$result")
                _updateState.value = UpdateState.Success
            }
        }
    }

    private fun Task?.isEqualTask(
        title: Task.Title,
        description: Task.Description,
        category: Categories,
        done: Boolean
    ): Boolean {
        when {
            this == null -> return false
            this.title.value != title.value -> return false
            this.description.value != description.value -> return false
            this.category != category -> return false
            this.done != done -> return false
        }

        return true
    }

}
