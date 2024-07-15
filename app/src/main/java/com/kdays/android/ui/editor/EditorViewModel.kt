package com.kdays.android.ui.editor

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.kdays.android.ext.StringExts.replaceJpegWithJpg
import com.kdays.android.logic.Repository
import com.kdays.android.logic.model.draft.drafts.Draft
import com.kdays.android.logic.model.editor.pub.PubRequest
import com.kdays.android.logic.model.editor.upload.UploadRequest
import com.luck.picture.lib.entity.LocalMedia
import java.io.File

class EditorViewModel : ViewModel() {

    val drafts = mutableListOf<Draft>()
    val images = mutableListOf<LocalMedia?>()

    // Pub
    lateinit var fid: String
    lateinit var selectedCat: String

    private val getPubLiveData = MutableLiveData<PubRequest>()

    private val getDraftsLiveData = MutableLiveData<String>()

    private val getRemoveDraftLiveData = MutableLiveData<Pair<String, String>>()

    private val getSaveDraftLiveData = MutableLiveData<Triple<String, String, String>>()

    private val getUploadLiveData = MutableLiveData<List<UploadRequest>>()

    private val getReplyLiveData = MutableLiveData<Triple<String, String, String>>()

    val pubLiveData =
        getPubLiveData.switchMap { request ->
            Repository.pub(request)
        }

    val draftsLiveData =
        getDraftsLiveData.switchMap { token ->
            Repository.getDrafts(token)
        }

    val removeDraftLiveData =
        getRemoveDraftLiveData.switchMap { (id, token) ->
            Repository.removeDraft(id, token)
        }

    val saveDraftLiveData =
        getSaveDraftLiveData.switchMap { (subject, content, token) ->
            Repository.saveDraft(subject, content, token)
        }

    val uploadLiveData =
        getUploadLiveData.switchMap { requests ->
            Repository.upload(requests)
        }

    val replyLiveData =
        getReplyLiveData.switchMap { (tid, content, token) ->
            Repository.reply(tid, content, token)
        }

    fun pub(fid: String, selectedCat: String, subject: String, content: String) {
        if (isTokenSaved()) {
            val token = getSavedToken()!!
            getPubLiveData.value = PubRequest(fid, selectedCat, subject, content, token)
        }
    }

    fun refreshDrafts() {
        if (isTokenSaved()) {
            val token = getSavedToken()!!
            getDraftsLiveData.value = token
        }
    }

    fun removeDraft(id: String) {
        if (isTokenSaved()) {
            val token = getSavedToken()!!
            getRemoveDraftLiveData.value = Pair(id, token)
        }
    }

    fun saveDraft(subject: String, content: String) {
        if (isTokenSaved()) {
            val token = getSavedToken()!!
            getSaveDraftLiveData.value = Triple(subject, content, token)
        }
    }

    fun upload(list: List<LocalMedia?>, fid: String) {
        images.addAll(list)

        if (isTokenSaved()) {
            val token = getSavedToken()!!
            val requests = list.mapNotNull { media ->
                return@mapNotNull if (media != null) {
                    val path = media.availablePath
                    val body = File(path).readBytes()
                    val uri = Uri.parse(path)
                    val uploadName = uri.lastPathSegment?.replaceJpegWithJpg()

                    return@mapNotNull if (uploadName != null) {
                        UploadRequest(body, token, fid, uploadName)
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
            getUploadLiveData.value = requests
        }
    }

    fun reply(tid: String, content: String) {
        if (isTokenSaved()) {
            val token = getSavedToken()!!
            getReplyLiveData.value = Triple(tid, content, token)
        }
    }

    // TokenDao
    private fun isTokenSaved() = Repository.isTokenSaved()

    private fun getSavedToken() = Repository.getSavedToken()
}