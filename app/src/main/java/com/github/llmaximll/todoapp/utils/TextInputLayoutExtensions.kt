package com.github.llmaximll.todoapp.utils

import androidx.annotation.StringRes
import com.google.android.material.textfield.TextInputLayout

private fun TextInputLayout.showErrorWithEnable(errorMessage: String) {
    this.isErrorEnabled = true
    this.error = errorMessage
}

fun TextInputLayout.showErrorResId(@StringRes stringResId: Int) =
    showErrorWithEnable(errorMessage = this.context.getString(stringResId))