package com.github.llmaximll.todoapp.domain.tasks.models

import androidx.room.ColumnInfo

data class TaskTitleId(
    val id: Long,

    val title: Task.Title,
)
