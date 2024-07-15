package com.kdays.android.logic.dao

import androidx.core.content.edit
import com.kdays.android.utils.StorageUtils.encryptedSharedPreferences

object InputDao {
    private const val INPUT = "input"

    fun saveInput(input: String) {
        encryptedSharedPreferences.edit {
            putString(INPUT, input)
        }
    }

    fun getSavedInput() = encryptedSharedPreferences.getString(INPUT, "")

    fun removeSavedInput() {
        encryptedSharedPreferences.edit {
            remove(INPUT)
        }
    }

    fun isInputSaved() = encryptedSharedPreferences.contains(INPUT)
}