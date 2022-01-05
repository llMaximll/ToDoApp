package com.github.llmaximll.todoapp.core

data class BuildConfigProvider(
    val debug: Boolean,
    val appId: String,
    val buildType: String,
    val versionCode: Int,
    val versionName: String,
)