package com.kdays.android.logic.dao

import androidx.core.content.edit
import com.kdays.android.utils.StorageUtils.encryptedSharedPreferences

object TokenExpiredDao {
    private const val TOKEN_EXPIRED = "token_expired"

    fun saveTokenExpired(tokenExpired: Long) {
        encryptedSharedPreferences.edit {
            putLong(TOKEN_EXPIRED, tokenExpired)
        }
    }

    fun getSavedTokenExpired() = encryptedSharedPreferences.getLong(TOKEN_EXPIRED, 0L)

    fun removeSavedTokenExpired() {
        encryptedSharedPreferences.edit {
            remove(TOKEN_EXPIRED)
        }
    }

    fun isTokenExpiredSaved() = encryptedSharedPreferences.contains(TOKEN_EXPIRED)
}