package com.github.llmaximll.todoapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigator
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.github.llmaximll.todoapp.work.NotifyWorker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

fun View.showSnackbar(
    @StringRes stringRes: Int,
    duration: Int = Snackbar.LENGTH_SHORT,
) {
    Snackbar.make(this, stringRes, duration).show()
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // do nothing
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // do nothing
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun NavController.safeNavigate(direction: NavDirections) {
    currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
}

fun NavController.safeNavigate(direction: NavDirections, extras: Navigator.Extras) {
    currentDestination?.getAction(direction.actionId)?.run { navigate(direction, extras) }
}

fun NavController.safeNavigate(
    @IdRes currentDestinationId: Int,
    @IdRes id: Int,
    args: Bundle? = null
) {
    if (currentDestinationId == currentDestination?.id) {
        navigate(id, args)
    }
}

fun EditText.showKeyboard() {
    this.requestFocus()
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun EditText.hideKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun Context.vectorToBitmap(drawableId: Int): Bitmap? {
    val drawable = AppCompatResources.getDrawable(this, drawableId) ?: return null
    val bitmap = createBitmap(
        drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    ) ?: return null
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun ViewModel.scheduleNotification(context: Context, title: String, workId: Long, date: Calendar) {
    viewModelScope.launch {
        val customTime: Long = date.timeInMillis
        val currentTime = System.currentTimeMillis()

        val data = Data.Builder().apply {
            putLong(NotifyWorker.NOTIFICATION_ID, 0)
            putLong(NotifyWorker.NOTIFICATION_WORK_ID, workId)
            putString(NotifyWorker.NOTIFICATION_SUBTITLE, title)
        }.build()
        val delay = customTime - currentTime

        val notificationWork = OneTimeWorkRequest.Builder(NotifyWorker::class.java).apply {
            setInitialDelay(delay, TimeUnit.MILLISECONDS)
            setInputData(data)
            addTag(workId.toString())
        }.build()

        val instanceWorkManager = WorkManager.getInstance(context)
        instanceWorkManager.beginUniqueWork(NotifyWorker.NOTIFICATION_WORK, ExistingWorkPolicy.APPEND_OR_REPLACE, notificationWork)
            .enqueue()
    }
}