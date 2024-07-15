package com.kdays.android.logic.network

import com.kdays.android.BuildConfig
import com.kdays.android.BuildConfig.DEBUG
import com.kdays.android.utils.LogUtils
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {

    private val client = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .build()

    val ucRetrofit = getRetrofit(BuildConfig.UC_ADDRESS)
    val bbsRetrofit = getRetrofit(BuildConfig.BBS_ADDRESS)

    // emotion api is only available for production environment
    val bbsProductionRetrofit = getRetrofit("https://bbs2.kdays.net")

    private fun getRetrofit(baseUrl: String): Retrofit {
        /*return LogUtils.getLoggingRetrofit(baseUrl)*/
        return if (!DEBUG) {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        } else {
            LogUtils.getLoggingRetrofit(baseUrl)
        }
    }
}