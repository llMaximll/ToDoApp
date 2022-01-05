package com.github.llmaximll.todoapp.data.explore

import com.github.llmaximll.todoapp.data.explore.local.Categories
import com.github.llmaximll.todoapp.data.explore.local.TaskEntity
import com.github.llmaximll.todoapp.domain.tasks.models.Category
import com.github.llmaximll.todoapp.domain.tasks.models.Task

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