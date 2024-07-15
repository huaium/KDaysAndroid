package com.kdays.android.logic.network

import com.google.gson.JsonObject
import com.kdays.android.logic.model.editor.upload.UploadResponse
import com.kdays.android.logic.network.ServiceCreator.bbsProductionRetrofit
import com.kdays.android.logic.network.ServiceCreator.bbsRetrofit
import com.kdays.android.logic.network.ServiceCreator.ucRetrofit
import com.kdays.android.logic.network.service.BbsService
import com.kdays.android.logic.network.service.BuyService
import com.kdays.android.logic.network.service.DraftService
import com.kdays.android.logic.network.service.EmotionService
import com.kdays.android.logic.network.service.ForumService
import com.kdays.android.logic.network.service.PubService
import com.kdays.android.logic.network.service.ReplyService
import com.kdays.android.logic.network.service.SearchService
import com.kdays.android.logic.network.service.TopTopicService
import com.kdays.android.logic.network.service.TopicInfoService
import com.kdays.android.logic.network.service.TopicListService
import com.kdays.android.logic.network.service.UcService
import com.kdays.android.logic.network.service.UploadService
import com.kdays.android.logic.network.service.UserInfoService
import com.kdays.android.logic.network.service.UserTopicsService
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.await
import retrofit2.create

object KDaysNetwork {

    // UC related
    private val ucService = ucRetrofit.create<UcService>()

    // BBS related
    private val bbsService = bbsRetrofit.create<BbsService>()
    private val userInfoService = bbsRetrofit.create<UserInfoService>()
    private val forumService = bbsRetrofit.create<ForumService>()
    private val topicListService = bbsRetrofit.create<TopicListService>()
    private val topicInfoService = bbsRetrofit.create<TopicInfoService>()
    private val replyService = bbsRetrofit.create<ReplyService>()
    private val pubService = bbsRetrofit.create<PubService>()
    private val buyService = bbsRetrofit.create<BuyService>()
    private val draftService = bbsRetrofit.create<DraftService>()
    private val uploadService = bbsRetrofit.create<UploadService>()
    private val userTopicsService = bbsRetrofit.create<UserTopicsService>()
    private val searchService = bbsRetrofit.create<SearchService>()

    // BBS related but only in production env
    private val emotionService = bbsProductionRetrofit.create<EmotionService>()
    private val topTopicService = bbsProductionRetrofit.create<TopTopicService>()


    suspend fun ucLogin(body: JsonObject, headers: Map<String, String>) =
        ucService.ucLogin(body, headers).await()

    suspend fun bbsLogin(body: JsonObject, headers: Map<String, String>) =
        bbsService.bbsLogin(body, headers).await()

    suspend fun getUserInfo(body: JsonObject, headers: Map<String, String>) =
        userInfoService.getUserInfo(body, headers).await()

    suspend fun getForumList(body: JsonObject, headers: Map<String, String>) =
        forumService.getForumList(body, headers).await()

    suspend fun getTopicList(
        body: JsonObject,
        headers: Map<String, String>,
    ) =
        topicListService.getTopicList(body, headers).await()

    suspend fun getTopicInfo(
        body: JsonObject,
        headers: Map<String, String>,
    ) =
        topicInfoService.getTopicInfo(body, headers).await()

    suspend fun reply(body: JsonObject, headers: Map<String, String>) =
        replyService.reply(body, headers).await()

    suspend fun pub(body: JsonObject, headers: Map<String, String>) =
        pubService.pub(body, headers).await()

    suspend fun getEmotion() = emotionService.getEmotion().await()

    suspend fun buy(body: JsonObject, headers: Map<String, String>) =
        buyService.buy(body, headers).await()

    suspend fun getDrafts(body: JsonObject, headers: Map<String, String>) =
        draftService.getDrafts(body, headers).await()

    suspend fun saveDraft(body: JsonObject, headers: Map<String, String>) =
        draftService.saveDraft(body, headers).await()

    suspend fun removeDraft(body: JsonObject, headers: Map<String, String>) =
        draftService.removeDraft(body, headers).await()

    suspend fun upload(
        body: ByteArray,
        headers: Map<String, String>,
        fid: String,
        uploadName: String
    ): UploadResponse {
        val requestBody = body.toRequestBody()
        return uploadService.upload(requestBody, headers, fid, uploadName).await()
    }

    suspend fun getUserTopics(
        body: JsonObject,
        headers: Map<String, String>,
    ) = userTopicsService.getUserTopics(body, headers).await()

    suspend fun getTopTopic(
        body: JsonObject,
        headers: Map<String, String>
    ) = topTopicService.getTopTopic(body, headers).await()

    suspend fun search(
        body: JsonObject,
        headers: Map<String, String>
    ) = searchService.search(body, headers).await()
}