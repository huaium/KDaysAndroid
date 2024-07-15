package com.kdays.android.ext

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.kdays.android.BuildConfig.BBS_ADDRESS
import com.kdays.android.KDaysApplication.Companion.context
import com.kdays.android.logic.dao.UseBrowserDao
import com.kdays.android.logic.model.urlparsed.OutLinkResult
import com.kdays.android.logic.model.urlparsed.ParsedResult
import com.kdays.android.logic.model.urlparsed.TopicInfoResult
import com.kdays.android.logic.model.urlparsed.TopicListResult
import com.kdays.android.ui.webview.WebViewActivity
import com.kdays.android.utils.UiUtils
import java.text.SimpleDateFormat
import java.time.DateTimeException
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object StringExts {

    // Date Parser
    @SuppressLint("SimpleDateFormat")
    private fun String.parseIso8601DateOld(): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE)

        val date = inputFormat.parse(this)
        return if (date != null) {
            outputFormat.format(date)
        } else this
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun String.parseIso8601Date(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        return try {
            val instant = Instant.parse(this)
            val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
            formatter.format(zonedDateTime)
        } catch (e: DateTimeException) {
            e.printStackTrace()
            this
        }
    }

    fun String.parseDate(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            parseIso8601Date()
        } else parseIso8601DateOld()
    }

    fun String.parseColor(): Int {
        val result = parseRGB()
        return if (result == -1) {
            parseHex()
        } else {
            result
        }
    }

    // Hex Color Parser
    // #000 000/#0000 0000 -> Color Int
    private fun String.parseHex(): Int {
        return try {
            Color.parseColor(this)
        } catch (e: Exception) {
            -1
        }
    }

    // RGB Color Parser
    // rgb(r,g,b) -> Color Int
    private fun String.parseRGB(): Int {
        val regex = Regex("rgb\\((\\d+), (\\d+), (\\d+)\\)")

        val matchResult = regex.find(this)
        if (matchResult != null && matchResult.groupValues.size == 4) {
            val red = matchResult.groupValues[1].toInt()
            val green = matchResult.groupValues[2].toInt()
            val blue = matchResult.groupValues[3].toInt()

            if (UiUtils.isDarkTheme() && red == 0 && green == 0 && blue == 255) {
                // avoid visual issue
                // #2196F3 -> rgb(33, 150, 243)
                return Color.rgb(33, 150, 243)
            }

            return Color.rgb(red, green, blue)
        }

        return -1
    }

    // Simpler Toast
    fun String.showToast(duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, this, duration).show()
    }

    fun String.openUrl(context: Context) {
        if (!UseBrowserDao.isBrowserUsed() && this.startsWith("https", ignoreCase = true)) {
            WebViewActivity.actionStart(context, this)
        } else {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = this.toUri()
            context.startActivity(intent)
        }
    }

    // Add base domain to url
    private fun String.addBase(baseUrl: String): Uri {
        val relativeUri = Uri.parse(this)

        return if (relativeUri.host.isNullOrEmpty()) {
            val combinedUrl = baseUrl + this
            val combinedUri = Uri.parse(combinedUrl)
            combinedUri
        } else {
            relativeUri
        }
    }

    // Parse forum url to topic or topic list
    fun String.parse(): ParsedResult {
        val uriWithBase = this.addBase(BBS_ADDRESS)

        if (uriWithBase.pathSegments.isEmpty()) {
            return OutLinkResult(uriWithBase.toString())
        }

        return when (uriWithBase.pathSegments.first()) {
            "read" -> {
                val tid = uriWithBase.pathSegments.last()
                val pid = uriWithBase.getQueryParameter("pid") ?: ""
                TopicInfoResult(tid, pid)
            }

            "thread" -> {
                val fid = uriWithBase.pathSegments.last()
                val page = uriWithBase.getQueryParameter("page") ?: ""
                TopicListResult(fid, page)
            }

            else -> OutLinkResult(uriWithBase.toString())
        }
    }

    // Replace suffix jpeg with jpg
    fun String.replaceJpegWithJpg(): String {
        val fileName = this.substringBeforeLast(".")
        val fileExt = this.substringAfterLast(".")
        return if (fileExt == "jpeg") {
            "$fileName.jpg"
        } else {
            this
        }
    }
}