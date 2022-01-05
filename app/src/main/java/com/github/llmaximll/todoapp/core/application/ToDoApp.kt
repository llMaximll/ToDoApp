package com.github.llmaximll.todoapp.core.application

import android.app.Application
import com.github.llmaximll.todoapp.core.BuildConfigProvider
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class ToDoApp : Application() {
    @Inject
    lateinit var buildConfig: BuildConfigProvider

    override fun onCreate() {
        super.onCreate()
        if (buildConfig.debug) {
            Timber.plant(Timber.DebugTree())
        }
    }
}