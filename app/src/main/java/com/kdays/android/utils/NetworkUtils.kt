package com.kdays.android.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.kdays.android.KDaysApplication.Companion.context
import com.kdays.android.ext.StringExts.showToast

object NetworkUtils {
    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    fun handleNetwork(onConnected: () -> Unit) {
        if (isNetworkConnected()) {
            onConnected()
        } else {
            "网络未连接，请重试".showToast()
        }
    }
}