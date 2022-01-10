package com.github.llmaximll.todoapp.presentation.details.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.llmaximll.todoapp.data.tasks.TasksRepository
import com.github.llmaximll.todoapp.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {
    var taskId: Long = -1L

    private val _state = MutableStateFlow<FetchDetailsState>(FetchDetailsState.Loading)
    val state: LiveData<FetchDetailsState>
        get() = _state
            .asLiveData(viewModelScope.coroutineContext)

    fun fetchData() {
        viewModelScope.launch {
            _state.value = handleTask()
        }
    }

    private suspend fun handleTask(): FetchDetailsState {
        return when (val result = tasksRepository.getTask(taskId)) {
            is Result.Error -> FetchDetailsState.Error(IllegalArgumentException("Task not found"))
            is Result.Success -> FetchDetailsState.Result(result.result)
        }
    }
}