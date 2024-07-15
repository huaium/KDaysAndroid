package com.kdays.android.logic.network.service

import com.google.gson.JsonObject
import com.kdays.android.logic.model.draft.drafts.DraftsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface DraftService {
    @POST("api/pub/drafts")
    fun getDrafts(
        @Body body: JsonObject,
        @HeaderMap headers: Map<String, String>
    ): Call<DraftsResponse>

    @POST("api/pub/saveDraft")
    fun saveDraft(
        @Body body: JsonObject,
        @HeaderMap headers: Map<String, String>
    ): Call<com.kdays.android.logic.model.draft.save.SaveResponse>

    @POST("api/pub/removeDraft")
    fun removeDraft(
        @Body body: JsonObject,
        @HeaderMap headers: Map<String, String>
    ): Call<com.kdays.android.logic.model.draft.remove.RemoveResponse>
}