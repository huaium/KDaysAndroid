package com.kdays.android.logic.dao

import androidx.core.content.edit
import com.kdays.android.utils.StorageUtils.encryptedSharedPreferences

object UserAvatarDao {
    private const val USER_AVATAR = "user_avatar"

    fun saveUserAvatar(userAvatar: String) {
        encryptedSharedPreferences.edit {
            putString(USER_AVATAR, userAvatar)
        }
    }

    fun getSavedUserAvatar() = encryptedSharedPreferences.getString(USER_AVATAR, "")

    fun removeSavedUserAvatar() {
        encryptedSharedPreferences.edit {
            remove(USER_AVATAR)
        }
    }

    fun isUserAvatarSaved() = encryptedSharedPreferences.contains(USER_AVATAR)
}