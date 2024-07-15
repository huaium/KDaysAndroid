package com.kdays.android.utils

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.text.Editable
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.kdays.android.KDaysApplication.Companion.context
import com.kdays.android.logic.dao.ThemeSelectorDao
import com.kdays.android.ui.settings.SettingsFragment

object UiUtils {

    val editableFactory: Editable.Factory = Editable.Factory.getInstance()

    fun isDarkTheme(): Boolean {
        return (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    fun setDarkTheme() {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
    }

    fun setLightTheme() {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
    }

    fun setAutoTheme() {
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun refreshTheme() {
        when (ThemeSelectorDao.getSelectedTheme()) {
            SettingsFragment.THEME_AUTO -> setAutoTheme()
            SettingsFragment.THEME_LIGHT -> setLightTheme()
            SettingsFragment.THEME_DARK -> setDarkTheme()
        }
    }

    fun getVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "N/A"
        }
    }
}