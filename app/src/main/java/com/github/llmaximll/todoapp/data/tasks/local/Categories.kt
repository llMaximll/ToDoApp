package com.github.llmaximll.todoapp.data.tasks.local

import android.content.Context
import androidx.annotation.StringRes
import com.github.llmaximll.todoapp.R

enum class Categories(@StringRes val value: Int) {
    BUSINESS(R.string.details_fragment_category_business),
    PERSONAL(R.string.details_fragment_category_personal),
    EDUCATION(R.string.details_fragment_category_education),
    SCIENCE(R.string.details_fragment_category_science);

    companion object {
        fun String.toCategory(context: Context): Categories =
            when (this) {
                context.getString(BUSINESS.value) -> BUSINESS
                context.getString(PERSONAL.value) -> PERSONAL
                context.getString(EDUCATION.value) -> EDUCATION
                else -> SCIENCE
            }
    }
}