package com.github.llmaximll.todoapp.utils

import com.github.llmaximll.todoapp.domain.tasks.models.Task

fun List<Task>.toListAll(): List<Task> = this.filter { it.done.not() }
fun List<Task>.toListOverdue(): List<Task> = this.filter { it.done }