package com.kdays.android.logic.dao

import androidx.core.content.edit
import com.kdays.android.utils.StorageUtils.encryptedSharedPreferences

object PasswordDao {
    private const val PASSWORD = "password"

    fun savePassword(password: String) {
        encryptedSharedPreferences.edit {
            putString(PASSWORD, password)
        }
    }

    fun getSavedPassword() = encryptedSharedPreferences.getString(PASSWORD, "")

    fun removeSavedPassword() {
        encryptedSharedPreferences.edit {
            remove(PASSWORD)
        }
    }

    fun isPasswordSaved() = encryptedSharedPreferences.contains(PASSWORD)
}