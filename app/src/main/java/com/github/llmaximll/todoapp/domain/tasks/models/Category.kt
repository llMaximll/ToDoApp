package com.github.llmaximll.todoapp.domain.tasks.models

data class Category(
    val id: Long,

    val title: String,

    val tasks: List<Task>
)
