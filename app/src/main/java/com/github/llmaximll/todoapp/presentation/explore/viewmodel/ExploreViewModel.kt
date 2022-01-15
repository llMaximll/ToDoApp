package com.github.llmaximll.todoapp.presentation.explore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.llmaximll.todoapp.data.tasks.TasksRepository
import com.github.llmaximll.todoapp.domain.tasks.models.Task
import com.github.llmaximll.todoapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    var currentList = emptyList<Task>()

    private val _filterState = MutableStateFlow(FilterTasks.ALL)
    val filterState: LiveData<FilterTasks>
        get() = _filterState
            .asLiveData(viewModelScope.coroutineContext)

    private val _tasksResult = MutableStateFlow<TasksResult>(TasksResult.EmptyResult)
    val tasksResult: LiveData<TasksResult>
        get() = _tasksResult
            .asLiveData(viewModelScope.coroutineContext)

    fun fetchData() {
        viewModelScope.launch {
            _tasksResult.value = TasksResult.Loading
            _tasksResult.value = handleTasks()
        }
    }

    private suspend fun handleTasks(): TasksResult {
        return when (val tasks = tasksRepository.getTasks()) {
            is Result.Error -> TasksResult.ErrorResult(IllegalArgumentException("Tasks not found"))
            is Result.Success -> if (tasks.result.isEmpty()){
                TasksResult.EmptyResult
            } else {
                TasksResult.SuccessResult(tasks.result)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.deleteTask(task)
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.insertTask(task)
        }
    }

    fun toggleFilterState(state: FilterTasks) {
        _filterState.value = state
    }
}