package com.github.llmaximll.todoapp.domain.tasks.models

import com.github.llmaximll.todoapp.data.tasks.local.Categories

data class Task(
    val id: Long,
    val title: Title,
    val description: Description,
    val category: Categories,
    val done: Boolean
) {
    class Title(val value: String) {
        companion object {
            fun createIfValid(value: String?): Title? {
                return if ((value != null)
                    && (value.isNotBlank())) {
                    Title(value)
                } else {
                    null
                }
            }
        }
    }

    class Description(val value: String) {
        companion object {
            fun createIfValid(value: String?): Description? {
                return if ((value != null)
                    && (value.isNotBlank())) {
                    Description(value)
                } else {
                    null
                }
            }
        }
    }
}
