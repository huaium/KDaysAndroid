package com.kdays.android.logic.dao

import androidx.core.content.edit
import com.kdays.android.utils.StorageUtils.sharedPreferences

object DeveloperDao {
    const val ENABLE_DEVELOPER = "enable_developer"

    fun enableDeveloper() {
        sharedPreferences.edit {
            putBoolean(ENABLE_DEVELOPER, true)
        }
    }

    fun isDeveloperEnabled() = sharedPreferences.getBoolean(ENABLE_DEVELOPER, false)
}