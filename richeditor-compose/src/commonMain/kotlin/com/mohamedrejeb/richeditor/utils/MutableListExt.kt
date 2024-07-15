package com.mohamedrejeb.richeditor.utils

internal fun <T> MutableList<T>.removeRange(start: Int, end: Int) {
    for (i in (end - 1) downTo start) {
        removeAt(i)
    }
}