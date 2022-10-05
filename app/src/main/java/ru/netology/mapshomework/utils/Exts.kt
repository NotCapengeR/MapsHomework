package ru.netology.mapshomework.utils

fun Throwable.getErrorMessage(): String = this.message ?: this.toString()
