package com.kdays.android.utils

import com.google.gson.JsonObject
import com.kdays.android.BuildConfig.BBS_KEY
import com.kdays.android.BuildConfig.BBS_SECRET
import com.kdays.android.BuildConfig.UC_KEY
import com.kdays.android.BuildConfig.UC_SECRET
import java.security.MessageDigest


object RequestUtils {
    enum class Entrance {
        UC, BBS
    }

    val emptyJson = JsonObject()

    fun getHeaders(
        body: JsonObject,
        entrance: Entrance,
        token: String = ""
    ): Map<String, String> {
        return getHeaders(body.toString().toByteArray(), entrance, token)
    }

    fun getHeaders(
        body: ByteArray,
        entrance: Entrance,
        token: String = ""
    ): Map<String, String> {
        val sigTime = TimeUtils.getCurrentTimeSec()
        val key = when (entrance) {
            Entrance.UC -> UC_KEY
            Entrance.BBS -> BBS_KEY
        }
        return mapOf(
            "X-API-KEY" to key,
            "X-SIGN" to sign(body, sigTime.toString().toByteArray(), entrance),
            "X-SIGN-TIME" to sigTime.toString(),
            "X-TOKEN" to token
        )
    }

    private fun sign(body: ByteArray, sigTime: ByteArray, entrance: Entrance): String {
        val secret = when (entrance) {
            Entrance.UC -> UC_SECRET
            Entrance.BBS -> BBS_SECRET
        }.toByteArray()
        val digest = MessageDigest.getInstance("SHA-1").apply {
            update(body)
            update(secret)
            update(sigTime)
        }.digest()
        return digest.joinToString("") { "%02x".format(it) }
    }
}