package com.kdays.android.ui.topicinfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import com.kdays.android.logic.Repository
import com.kdays.android.logic.model.topicinfo.Post
import kotlin.properties.Delegates

class TopicInfoViewModel(val tid: String) : ViewModel() {
    @Suppress("UNCHECKED_CAST")
    class Factory(private val tid: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TopicInfoViewModel::class.java)) {
                return TopicInfoViewModel(tid) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val postList = mutableListOf<Post>()

    var currentPage = 1
    var pageTotal by Delegates.notNull<Int>()
    var pageSize by Delegates.notNull<Int>()
    var itemTotal by Delegates.notNull<Int>()

    private val getTopicInfoLiveData = MutableLiveData<Pair<String, String>>()

    private val getBuyLiveData = MutableLiveData<Triple<String, String, String>>()

    val topicInfoLiveData =
        getTopicInfoLiveData.switchMap { (page, token) ->
            Repository.getTopicInfo(tid, page, token)
        }

    val buyLiveData =
        getBuyLiveData.switchMap { (tid, pid, token) ->
            Repository.buy(tid, pid, token)
        }

    fun getTopicInfo(page: String) {
        if (isTokenSaved()) {
            val token = getSavedToken()!!
            getTopicInfoLiveData.value = Pair(page, token)
        }
    }

    fun buy(tid: String, pid: String) {
        if (isTokenSaved()) {
            val token = getSavedToken()!!
            getBuyLiveData.value = Triple(tid, pid, token)
        }
    }

    // Storage
    private fun getSavedToken() = Repository.getSavedToken()

    private fun isTokenSaved() = Repository.isTokenSaved()

    fun noCache(): Boolean {
        return postList.isEmpty()
    }
}