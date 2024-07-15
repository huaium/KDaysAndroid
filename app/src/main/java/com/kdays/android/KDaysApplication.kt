package com.kdays.android

import android.app.Application
import android.content.Context
import com.kdays.android.utils.UiUtils

class KDaysApplication : Application() {

    companion object {
        @SuppressWarnings("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        UiUtils.refreshTheme()
    }
}