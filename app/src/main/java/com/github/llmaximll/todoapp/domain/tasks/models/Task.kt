package com.github.llmaximll.todoapp.domain.tasks.models

import com.github.llmaximll.todoapp.data.explore.local.Categories

data class Task(
    val id: Long,
    val title: String,
    val description: String,
    val category: String,
    val done: Boolean
)
