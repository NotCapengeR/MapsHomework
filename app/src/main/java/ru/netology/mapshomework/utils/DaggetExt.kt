package ru.netology.mapshomework.utils

import android.content.Context
import androidx.fragment.app.Fragment
import ru.netology.mapshomework.App
import ru.netology.mapshomework.core.di.AppComponent

fun Fragment.getAppComponent(): AppComponent =
    (requireContext().applicationContext as App).appComponent

fun Context.getAppComponent(): AppComponent = when (this) {
    is App -> appComponent
    else -> (applicationContext as App).appComponent
}