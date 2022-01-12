package com.github.llmaximll.todoapp.data.tasks

import com.github.llmaximll.todoapp.data.tasks.local.Categories
import com.github.llmaximll.todoapp.data.tasks.local.TaskEntity
import com.github.llmaximll.todoapp.domain.tasks.models.Category
import com.github.llmaximll.todoapp.domain.tasks.models.Task

internal fun Task.toEntity(): TaskEntity =
    TaskEntity(
        id = this.id,
        title = this.title.value,
        description = this.description.value,
        category = this.category,
        done = this.done,
        date = this.date
    )