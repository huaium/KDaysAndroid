package com.kdays.android.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LogUtils {

    // Common logging
    private const val VERBOSE = 1
    private const val DEBUG = 2
    private const val INFO = 3
    private const val WARN = 4
    private const val ERROR = 5
    private var level = VERBOSE

    fun v(tag: String, msg: String) {
        if (level <= VERBOSE) {
            Log.v(tag, msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (level <= DEBUG) {
            Log.d(tag, msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (level <= INFO) {
            Log.i(tag, msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (level <= WARN) {
            Log.w(tag, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (level <= ERROR) {
            Log.e(tag, msg)
        }
    }

    // Retrofit logging
    fun getLoggingRetrofit(baseUrl: String): Retrofit {
        val gson: Gson = GsonBuilder().setLenient().create()

        val loggingInterceptor = Interceptor { chain ->
            val request: Request = chain.request()
            val requestUrl = request.url
            val requestHeaders = request.headers
            val requestBody = request.body

            // Check if the request has a body and its content type is JSON
            if (requestBody != null && requestBody.contentType()?.subtype == "json") {
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                val requestBodyString = buffer.readUtf8()

                // Output request url
                d("retrofit", "Request Url: $requestUrl")

                // Output headers and the raw JSON data of the request
                d("retrofit", "Request Headers: $requestHeaders")
                d("retrofit", "Request JSON: $requestBodyString")
            }

            val response: Response = chain.proceed(request)
            val bodyString = response.body?.string() ?: ""
            val contentType = response.body?.contentType()

            // Output raw JSON data of the response
            d("retrofit", "Response JSON: $bodyString")

            val newResponseBody = bodyString.toResponseBody(contentType)
            response.newBuilder().body(newResponseBody).build()
        }

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}