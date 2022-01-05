package com.github.llmaximll.todoapp.data.explore.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TaskEntity.TABLE_NAME)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = TASK_ID)
    val id: Long,

    @ColumnInfo(name = TITLE)
    val title: String,

    @ColumnInfo(name = DESCRIPTION)
    val description: String,

    @ColumnInfo(name = CATEGORY)
    val category: Categories,

    @ColumnInfo(name = DONE)
    val done: Boolean
) {
    companion object {
        const val TABLE_NAME = "tasks"

        const val TASK_ID = "task_id"
        const val TITLE = "title"
        const val DESCRIPTION = "description"
        const val CATEGORY = "category"
        const val DONE = "done"
    }
}
