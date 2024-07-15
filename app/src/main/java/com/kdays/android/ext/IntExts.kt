package com.kdays.android.ext

object IntExts {
    fun Int.divideRoundUp(divisor: Int): Int {
        val total = this + 1
        val result = total / divisor
        return if (total % divisor == 0) {
            result
        } else result + 1
    }
}