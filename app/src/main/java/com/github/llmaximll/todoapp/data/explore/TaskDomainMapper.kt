package com.github.llmaximll.todoapp.data.explore

import com.github.llmaximll.todoapp.data.explore.local.TaskEntity
import com.github.llmaximll.todoapp.domain.tasks.models.Task

internal fun TaskEntity.toModel(): Task =
    Task(
        id = this.id,
        title = this.title,
        description = this.description,
        category = this.category.name,
        done = this.done
    )