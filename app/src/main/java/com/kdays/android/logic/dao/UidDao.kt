package com.kdays.android.logic.dao

import androidx.core.content.edit
import com.kdays.android.utils.StorageUtils

object UidDao {
    private const val UID = "uid"

    fun saveUid(uid: String) {
        StorageUtils.encryptedSharedPreferences.edit {
            putString(UID, uid)
        }
    }

    fun getSavedUid() = StorageUtils.encryptedSharedPreferences.getString(UID, "")

    fun removeSavedUid() {
        StorageUtils.encryptedSharedPreferences.edit {
            remove(UID)
        }
    }

    fun isUidSaved() = StorageUtils.encryptedSharedPreferences.contains(UID)
}