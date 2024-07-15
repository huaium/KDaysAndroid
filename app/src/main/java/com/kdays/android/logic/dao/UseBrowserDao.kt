package com.kdays.android.logic.dao

import com.kdays.android.ui.settings.SettingsFragment
import com.kdays.android.utils.StorageUtils.sharedPreferences

object UseBrowserDao {
    fun isBrowserUsed() =
        sharedPreferences.getBoolean(SettingsFragment.USE_BROWSER, false)
}