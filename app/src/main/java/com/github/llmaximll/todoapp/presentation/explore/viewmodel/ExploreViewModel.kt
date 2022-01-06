package com.github.llmaximll.todoapp.presentation.explore.viewmodel

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
class ExploreViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _result = MutableStateFlow<CategoriesResult>(CategoriesResult.EmptyResult)
    val result: LiveData<CategoriesResult>
        get() = _result
            .asLiveData(viewModelScope.coroutineContext)

    init {
        viewModelScope.launch {
            _result.value = CategoriesResult.Loading
            _result.value = handleCategories()
        }
    }

    private suspend fun handleCategories(): CategoriesResult {
        return when (val categories = tasksRepository.getCategories()) {
            is Result.Error -> CategoriesResult.ErrorResult(IllegalArgumentException("Tasks not found"))
            is Result.Success -> if (categories.result.isEmpty())
                CategoriesResult.EmptyResult
            else
                CategoriesResult.SuccessResult(categories.result)
        }
    }
}