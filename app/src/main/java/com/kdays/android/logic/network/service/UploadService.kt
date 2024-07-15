package com.kdays.android.logic.network.service

import com.kdays.android.logic.model.editor.upload.UploadResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Query

interface UploadService {
    @POST("api/attach/upload")
    fun upload(
        @Body body: RequestBody,
        @HeaderMap headers: Map<String, String>,
        @Query("fid") fid: String,
        @Query("uploadName") uploadName: String
    ): Call<UploadResponse>
}