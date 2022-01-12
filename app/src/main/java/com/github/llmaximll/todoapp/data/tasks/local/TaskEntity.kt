package com.github.llmaximll.todoapp.data.tasks.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = TaskEntity.TABLE_NAME, indices = [ Index(value = [ TaskEntity.TITLE ], unique = true) ])
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
    val done: Boolean,

    @ColumnInfo(name = DATE)
    val date: Long
) {
    companion object {
        const val TABLE_NAME = "tasks"

        const val TASK_ID = "task_id"
        const val TITLE = "title"
        const val DESCRIPTION = "description"
        const val CATEGORY = "category"
        const val DONE = "done"
        const val DATE = "date"
    }
}
