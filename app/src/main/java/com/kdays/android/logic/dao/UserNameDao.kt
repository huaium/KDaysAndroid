package com.kdays.android.logic.dao

import androidx.core.content.edit
import com.kdays.android.utils.StorageUtils.encryptedSharedPreferences

object UserNameDao {
    private const val USER_NAME = "user_name"

    fun saveUserName(userName: String) {
        encryptedSharedPreferences.edit {
            putString(USER_NAME, userName)
        }
    }

    fun getSavedUserName() = encryptedSharedPreferences.getString(USER_NAME, "")

    fun removeSavedUserName() {
        encryptedSharedPreferences.edit {
            remove(USER_NAME)
        }
    }

    fun isUserNameSaved() = encryptedSharedPreferences.contains(USER_NAME)
}