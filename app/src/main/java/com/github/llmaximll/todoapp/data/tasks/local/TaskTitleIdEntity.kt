package com.github.llmaximll.todoapp.data.tasks.local

import androidx.room.ColumnInfo

data class TaskTitleIdEntity(
    @ColumnInfo(name = TaskEntity.TASK_ID)
    val id: Long,

    @ColumnInfo(name = TaskEntity.TITLE)
    val title: String,
) {
    companion object {
        const val TASK_ID = "task_id"
        const val TITLE = "title"
    }
}
