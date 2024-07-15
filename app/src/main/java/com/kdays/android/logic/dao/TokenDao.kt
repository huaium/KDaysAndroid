package com.kdays.android.logic.dao

import androidx.core.content.edit
import com.kdays.android.utils.StorageUtils.encryptedSharedPreferences

object TokenDao {
    private const val ACCESS_TOKEN = "access_token"

    fun saveToken(token: String) {
        encryptedSharedPreferences.edit {
            putString(ACCESS_TOKEN, token)
        }
    }

    fun getSavedToken() = encryptedSharedPreferences.getString(ACCESS_TOKEN, "")

    fun removeSavedToken() {
        encryptedSharedPreferences.edit {
            remove(ACCESS_TOKEN)
        }
    }

    fun isTokenSaved() = encryptedSharedPreferences.contains(ACCESS_TOKEN)
}