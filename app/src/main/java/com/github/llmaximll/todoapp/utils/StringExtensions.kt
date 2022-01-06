package com.github.llmaximll.todoapp.utils

import java.util.*

fun String.titlecaseFirstCharIfItIsLowercase() =
    this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }