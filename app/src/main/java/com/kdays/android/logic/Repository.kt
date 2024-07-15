package com.kdays.android.logic

import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kdays.android.BuildConfig.UC_KEY
import com.kdays.android.logic.dao.InputDao
import com.kdays.android.logic.dao.PasswordDao
import com.kdays.android.logic.dao.TokenDao
import com.kdays.android.logic.dao.TokenExpiredDao
import com.kdays.android.logic.dao.UidDao
import com.kdays.android.logic.dao.UserAvatarDao
import com.kdays.android.logic.dao.UserNameDao
import com.kdays.android.logic.exception.UserCausedException
import com.kdays.android.logic.exception.UserCausedException.BbsErrorCode.AVATAR_NOT_SET
import com.kdays.android.logic.exception.UserCausedException.BbsErrorCode.BBS_ACCOUNT_NOT_CREATED
import com.kdays.android.logic.exception.UserCausedException.UcErrorCode.UNAUTHORIZED
import com.kdays.android.logic.model.bbslogin.BbsLoginData
import com.kdays.android.logic.model.bbslogin.BbsLoginRequest
import com.kdays.android.logic.model.editor.pub.PubData
import com.kdays.android.logic.model.editor.pub.PubRequest
import com.kdays.android.logic.model.editor.reply.ReplyData
import com.kdays.android.logic.model.editor.upload.UploadRequest
import com.kdays.android.logic.model.uclogin.UcLoginData
import com.kdays.android.logic.model.uclogin.UnauthorizedData
import com.kdays.android.logic.network.KDaysNetwork
import com.kdays.android.utils.RequestUtils
import com.kdays.android.utils.RequestUtils.Entrance.BBS
import com.kdays.android.utils.RequestUtils.Entrance.UC
import com.kdays.android.utils.RequestUtils.emptyJson
import com.kdays.android.utils.RequestUtils.getHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

object Repository {

    // Network Repository
    fun ucLogin(username: String, password: String) =
        liveData(Dispatchers.IO) {
            val result = try {
                val body = JsonObject().apply {
                    addProperty("input", username)
                    addProperty("password", password)
                }
                val loginResponse = KDaysNetwork.ucLogin(body, getHeaders(body, UC))
                when (loginResponse.code) {
                    0 -> {
                        // Now we can assert that data is not an array
                        val data = Gson().fromJson(
                            loginResponse.data,
                            UcLoginData::class.java
                        )
                        Result.success(BbsLoginRequest(data.accessToken, username, password))
                    }

                    UNAUTHORIZED -> {
                        val data = Gson().fromJson(loginResponse.data, UnauthorizedData::class.java)
                        Result.failure(UserCausedException(data.authorizeUrl, UNAUTHORIZED))
                    }

                    else ->
                        Result.failure(
                            UserCausedException(
                                loginResponse.msg,
                                loginResponse.code
                            )
                        )
                }
            } catch (e: Exception) {
                // Underlying exception that is not caused by user
                Result.failure(e)
            }
            emit(result)
        }

    fun bbsLogin(bbsLoginRequest: BbsLoginRequest) = liveData(Dispatchers.IO) {
        val result = try {
            val body = JsonObject().apply {
                addProperty("p_name", "kduc")
                addProperty("do", "appAuthorize")
                addProperty("apikey", UC_KEY)
                addProperty("token", bbsLoginRequest.accessToken)
            }
            val loginResponse = KDaysNetwork.bbsLogin(body, getHeaders(body, BBS))
            when (loginResponse.code) {
                0 -> {
                    // Now we can assert that data is not an array
                    val tokenPair = Gson().fromJson(
                        loginResponse.data,
                        BbsLoginData::class.java
                    )
                    Result.success(
                        Triple(
                            tokenPair,
                            bbsLoginRequest.username,
                            bbsLoginRequest.password
                        )
                    )
                }

                AVATAR_NOT_SET -> {
                    Result.failure(UserCausedException("avatar not set", AVATAR_NOT_SET))
                }

                BBS_ACCOUNT_NOT_CREATED -> {
                    Result.failure(
                        UserCausedException(
                            "bbs account not created",
                            BBS_ACCOUNT_NOT_CREATED
                        )
                    )
                }

                else ->
                    Result.failure(
                        UserCausedException(
                            loginResponse.msg,
                            loginResponse.code
                        )
                    )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }

    fun getUserInfo(bbsToken: String) = liveData(Dispatchers.IO) {
        val result = try {
            val userInfoResponse = KDaysNetwork.getUserInfo(
                emptyJson,
                getHeaders(emptyJson, BBS, bbsToken)
            )
            if (userInfoResponse.code == 0) {
                val userInfoData = userInfoResponse.data
                Result.success(userInfoData)
            } else {
                Result.failure(
                    UserCausedException(
                        userInfoResponse.msg,
                        userInfoResponse.code
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }

    fun getForumList(bbsToken: String) = liveData(Dispatchers.IO) {
        val result = try {
            val forumResponse = KDaysNetwork.getForumList(
                emptyJson,
                getHeaders(emptyJson, BBS, bbsToken)
            )
            if (forumResponse.code == 0) {
                val outerForumList = forumResponse.data.items
                Result.success(outerForumList)
            } else {
                Result.failure(
                    UserCausedException(
                        forumResponse.msg,
                        forumResponse.code
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }

    fun getTopicList(fid: String, page: String, token: String) =
        liveData(Dispatchers.IO) {
            val result = try {
                val body = JsonObject().apply {
                    addProperty("fid", fid)
                    addProperty("page", page)
                }
                val topicListResponse = KDaysNetwork.getTopicList(
                    body,
                    getHeaders(body, BBS, token)
                )
                if (topicListResponse.code == 0) {
                    val topicListData = topicListResponse.data
                    Result.success(topicListData)
                } else {
                    Result.failure(
                        UserCausedException(
                            topicListResponse.msg,
                            topicListResponse.code
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(result)
        }

    fun getTopicInfo(tid: String, page: String, token: String) =
        liveData(Dispatchers.IO) {
            val result = try {
                val body = JsonObject().apply {
                    addProperty("tid", tid)
                    addProperty("page", page)
                }
                val topicInfoResponse = KDaysNetwork.getTopicInfo(
                    body,
                    getHeaders(body, BBS, token)
                )
                if (topicInfoResponse.code == 0) {
                    val topicInfoData = topicInfoResponse.data
                    Result.success(topicInfoData)
                } else {
                    Result.failure(
                        UserCausedException(
                            topicInfoResponse.msg,
                            topicInfoResponse.code
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(result)
        }

    fun reply(tid: String, content: String, token: String) =
        liveData(Dispatchers.IO) {
            val result = try {
                val body = JsonObject().apply {
                    addProperty("tid", tid)
                    addProperty("content", content)
                }
                val replyResponse = KDaysNetwork.reply(body, getHeaders(body, BBS, token))
                when (replyResponse.code) {
                    0 -> {
                        // Now we can assert that data is not an array
                        val replyData = Gson().fromJson(
                            replyResponse.data,
                            ReplyData::class.java
                        )
                        Result.success(replyData)
                    }

                    else ->
                        Result.failure(
                            UserCausedException(
                                replyResponse.msg,
                                replyResponse.code
                            )
                        )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(result)
        }

    fun pub(pubRequest: PubRequest) =
        liveData(Dispatchers.IO) {
            val result = try {
                val body = JsonObject().apply {
                    addProperty("fid", pubRequest.fid)
                    // only special 0 can publish topics
                    addProperty("special", "0")
                    addProperty("cat", pubRequest.selectedCat)
                    addProperty("subject", pubRequest.subject)
                    addProperty("content", pubRequest.content)
                }
                val pubResponse = KDaysNetwork.pub(body, getHeaders(body, BBS, pubRequest.token))
                when (pubResponse.code) {
                    0 -> {
                        // Now we can assert that data is not an array
                        val pubData = Gson().fromJson(
                            pubResponse.data,
                            PubData::class.java
                        )
                        Result.success(pubData)
                    }

                    else ->
                        Result.failure(
                            UserCausedException(
                                pubResponse.msg,
                                pubResponse.code
                            )
                        )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(result)
        }

    suspend fun getEmotion() =
        try {
            val emotionResponse = KDaysNetwork.getEmotion()
            if (emotionResponse.code == 0) {
                val emotionData = emotionResponse.data
                Result.success(emotionData)
            } else {
                Result.failure(
                    UserCausedException(
                        emotionResponse.msg,
                        emotionResponse.code
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    fun buy(tid: String, pid: String, token: String) =
        liveData(Dispatchers.IO) {
            val result = try {
                val body = JsonObject().apply {
                    addProperty("tid", tid)
                    addProperty("pid", pid)
                }
                val buyResponse = KDaysNetwork.buy(body, getHeaders(body, BBS, token))
                if (buyResponse.code == 0) {
                    Result.success("购买成功")
                } else {
                    Result.failure(UserCausedException(buyResponse.msg, buyResponse.code))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(result)
        }

    fun getDrafts(token: String) =
        liveData(Dispatchers.IO) {
            val result = try {
                val draftsResponse =
                    KDaysNetwork.getDrafts(emptyJson, getHeaders(emptyJson, BBS, token))
                if (draftsResponse.code == 0) {
                    Result.success(draftsResponse.data)
                } else {
                    Result.failure(
                        UserCausedException(
                            draftsResponse.msg,
                            draftsResponse.code
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(result)
        }

    fun saveDraft(subject: String, content: String, token: String) =
        liveData(Dispatchers.IO) {
            val result = try {
                val body = JsonObject().apply {
                    addProperty("subject", subject)
                    addProperty("content", content)
                    addProperty("autosave", "0")
                }
                val saveDraftResponse = KDaysNetwork.saveDraft(body, getHeaders(body, BBS, token))
                if (saveDraftResponse.code == 0) {
                    Result.success("保存成功")
                } else {
                    Result.failure(
                        UserCausedException(
                            saveDraftResponse.msg,
                            saveDraftResponse.code
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(result)
        }

    fun removeDraft(id: String, token: String) =
        liveData(Dispatchers.IO) {
            val result = try {
                val body = JsonObject().apply {
                    addProperty("id", id)
                }
                val removeDraftResponse = KDaysNetwork.removeDraft(
                    body,
                    getHeaders(body, RequestUtils.Entrance.BBS, token)
                )
                if (removeDraftResponse.code == 0) {
                    Result.success("删除成功")
                } else {
                    Result.failure(
                        UserCausedException(
                            removeDraftResponse.msg,
                            removeDraftResponse.code
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(result)
        }

    fun upload(requests: List<UploadRequest>) =
        liveData(Dispatchers.IO) {
            val deferredUploads = requests.map { request ->
                withContext(Dispatchers.IO) {
                    async {
                        val result = try {
                            val uploadResponse = KDaysNetwork.upload(
                                request.body,
                                getHeaders(
                                    request.body,
                                    BBS,
                                    request.token
                                ),
                                request.fid,
                                request.uploadName
                            )
                            if (uploadResponse.code == 0) {
                                Result.success(uploadResponse.data)
                            } else {
                                Result.failure(
                                    UserCausedException(
                                        uploadResponse.msg,
                                        uploadResponse.code
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            Result.failure(e)
                        }
                        result
                    }
                }
            }
            emit(deferredUploads.awaitAll())
        }

    fun getUserTopics(uid: String, page: String, token: String) =
        liveData(Dispatchers.IO) {
            val result = try {
                val body = JsonObject().apply {
                    addProperty("uid", uid)
                    addProperty("page", page)
                }
                val userTopicsResponse = KDaysNetwork.getUserTopics(
                    body, getHeaders(body, BBS, token)
                )
                if (userTopicsResponse.code == 0) {
                    Result.success(userTopicsResponse.data)
                } else {
                    Result.failure(
                        UserCausedException(
                            userTopicsResponse.msg,
                            userTopicsResponse.code
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(result)
        }

    fun getTopTopic() = liveData(Dispatchers.IO) {
        val result = try {
            val topTopicResponse = KDaysNetwork.getTopTopic(
                emptyJson,
                getHeaders(emptyJson, BBS)
            )
            if (topTopicResponse.code == 0) {
                val topTopicData = topTopicResponse.data
                Result.success(topTopicData)
            } else {
                Result.failure(
                    UserCausedException(
                        topTopicResponse.msg,
                        topTopicResponse.code
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }

    fun search(keyword: String, bbsToken: String) = liveData(Dispatchers.IO) {
        val result = try {
            val body = JsonObject().apply {
                addProperty("keyword", keyword)
            }
            val searchResponse = KDaysNetwork.search(
                body,
                getHeaders(body, BBS, bbsToken)
            )
            if (searchResponse.code == 0) {
                val searchedTopics = searchResponse.data.items
                Result.success(searchedTopics)
            } else {
                Result.failure(
                    UserCausedException(
                        searchResponse.msg,
                        searchResponse.code
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }

    // Dao Repository
    fun removeAllDao() {
        removeSavedToken()
        removeSavedTokenExpired()
        removeSavedUserAvatar()
        removeSavedUserName()
        removeSavedInput()
        removeSavedPassword()
        removeSavedUid()
    }

    // TokenDao
    fun saveToken(token: String) = TokenDao.saveToken(token)

    fun getSavedToken() = TokenDao.getSavedToken()

    fun removeSavedToken() = TokenDao.removeSavedToken()

    fun isTokenSaved() = TokenDao.isTokenSaved()

    // TokenExpiredDao
    fun saveTokenExpired(tokenExpired: Long) = TokenExpiredDao.saveTokenExpired(tokenExpired)

    fun getSavedTokenExpired() = TokenExpiredDao.getSavedTokenExpired()

    fun removeSavedTokenExpired() = TokenExpiredDao.removeSavedTokenExpired()

    fun isTokenExpiredSaved() = TokenExpiredDao.isTokenExpiredSaved()

    // UserAvatarDao
    fun saveUserAvatar(userAvatar: String) = UserAvatarDao.saveUserAvatar(userAvatar)

    fun getSavedUserAvatar() = UserAvatarDao.getSavedUserAvatar()

    private fun removeSavedUserAvatar() = UserAvatarDao.removeSavedUserAvatar()

    fun isUserAvatarSaved() = UserAvatarDao.isUserAvatarSaved()

    // UserNameDao
    fun saveUserName(userName: String) = UserNameDao.saveUserName(userName)

    fun getSavedUserName() = UserNameDao.getSavedUserName()

    private fun removeSavedUserName() = UserNameDao.removeSavedUserName()

    fun isUserNameSaved() = UserNameDao.isUserNameSaved()

    // InputDao
    fun saveInput(input: String) = InputDao.saveInput(input)

    fun getSavedInput() = InputDao.getSavedInput()

    private fun removeSavedInput() = InputDao.removeSavedInput()

    fun isInputSaved() = InputDao.isInputSaved()

    // PasswordDao
    fun savePassword(password: String) = PasswordDao.savePassword(password)

    fun getSavedPassword() = PasswordDao.getSavedPassword()

    private fun removeSavedPassword() = PasswordDao.removeSavedPassword()

    fun isPasswordSaved() = PasswordDao.isPasswordSaved()

    // UidDao
    fun saveUid(uid: String) = UidDao.saveUid(uid)

    fun getSavedUid() = UidDao.getSavedUid()

    private fun removeSavedUid() = UidDao.removeSavedUid()

    fun isUidSaved() = UidDao.isUidSaved()
}