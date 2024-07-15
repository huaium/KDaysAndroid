package com.kdays.android.logic.dao

import com.kdays.android.ui.settings.SettingsFragment
import com.kdays.android.utils.StorageUtils.sharedPreferences

object TracingDao {
    fun isTracing() =
        sharedPreferences.getBoolean(SettingsFragment.METHOD_TRACING, false)
}