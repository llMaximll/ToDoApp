package com.github.llmaximll.todoapp.data.tasks.local

enum class Categories(val value: String) {
    BUSINESS("Business"),
    PERSONAL("Personal"),
    EDUCATION("Education"),
    SCIENCE("Science");

    companion object {
        fun String.toCategory(): Categories =
            when (this) {
                BUSINESS.value -> BUSINESS
                PERSONAL.value -> PERSONAL
                EDUCATION.value -> EDUCATION
                else -> SCIENCE
            }
    }
}