package com.github.llmaximll.todoapp.data.tasks

import com.github.llmaximll.todoapp.data.tasks.local.Categories
import com.github.llmaximll.todoapp.data.tasks.local.TaskEntity
import com.github.llmaximll.todoapp.data.tasks.local.TaskTitleIdEntity
import com.github.llmaximll.todoapp.domain.tasks.models.Category
import com.github.llmaximll.todoapp.domain.tasks.models.Task
import com.github.llmaximll.todoapp.domain.tasks.models.TaskTitleId

internal fun TaskEntity.toModel(): Task =
    Task(
        id = this.id,
        title = Task.Title(this.title),
        description = Task.Description(this.description),
        category = this.category,
        done = this.done,
        date = this.date
    )

internal fun TaskTitleIdEntity.toModel(): TaskTitleId =
    TaskTitleId(
        id = this.id,
        title = Task.Title(this.title)
    )

internal fun List<TaskEntity?>.toCategories(): List<Category> {
    val businessList = mutableListOf<Task>()
    val personalList = mutableListOf<Task>()
    val educationList = mutableListOf<Task>()
    val scienceList = mutableListOf<Task>()
    this.forEach { task ->
        when (task?.category) {
            Categories.BUSINESS -> businessList.add(task.toModel())
            Categories.PERSONAL -> personalList.add(task.toModel())
            Categories.EDUCATION -> educationList.add(task.toModel())
            Categories.SCIENCE -> scienceList.add(task.toModel())
        }
    }

    return listOf(
        Category(id = 0, title = "Business", tasks = businessList),
        Category(id = 1, title = "Personal", tasks = personalList),
        Category(id = 2, title = "Education", tasks = educationList),
        Category(id = 3, title = "Science", tasks = scienceList)
    )
}