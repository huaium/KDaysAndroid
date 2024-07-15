package com.kdays.android.logic.dao

import com.kdays.android.ui.settings.SettingsFragment.Companion.THEME_SELECTOR
import com.kdays.android.utils.StorageUtils.sharedPreferences

object ThemeSelectorDao {
    fun getSelectedTheme() =
        sharedPreferences.getString(THEME_SELECTOR, "auto")
}