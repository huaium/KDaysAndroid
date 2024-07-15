package com.kdays.android.utils

import android.os.Build
import java.time.Instant

object TimeUtils {
    fun getCurrentTimeSec(): Long {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant.now().epochSecond
        } else System.currentTimeMillis() / 1000
    }
}