package com.kdays.android.utils

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.kdays.android.KDaysApplication.Companion.context

object StorageUtils {
    val encryptedSharedPreferences: SharedPreferences by lazy {
        encryptedSharedPreferences()
    }

    val sharedPreferences: SharedPreferences by lazy {
        sharedPreferences()
    }

    private fun encryptedSharedPreferences(): SharedPreferences {
        val keyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedSharedPreferences.create(
            "kdays",
            keyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun sharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}