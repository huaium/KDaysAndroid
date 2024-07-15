package com.kdays.android.ui.topiclist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import com.kdays.android.logic.Repository
import com.kdays.android.logic.model.topiclist.Topic
import kotlin.properties.Delegates

class TopicListViewModel(val fid: String) : ViewModel() {
    @Suppress("UNCHECKED_CAST")
    class Factory(private val fid: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TopicListViewModel::class.java)) {
                return TopicListViewModel(fid) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val toppedTopicList = mutableListOf<Topic>()

    val normalTopicList = mutableListOf<Topic>()

    val admins = mutableListOf<String>()

    var pageTotal by Delegates.notNull<Int>()

    var catList: String? = null

    private val getTopicListLiveData = MutableLiveData<Triple<String, String, String>>()

    val topicListLiveData =
        getTopicListLiveData.switchMap { (fid, page, token) ->
            Repository.getTopicList(
                fid,
                page,
                token
            )
        }

    fun getTopicList(page: String) {
        if (isTokenSaved()) {
            val token = getSavedToken()!!
            getTopicListLiveData.value = Triple(fid, page, token)
        }
    }

    // Storage
    private fun getSavedToken() = Repository.getSavedToken()

    private fun isTokenSaved() = Repository.isTokenSaved()

    fun noCache(): Boolean {
        return toppedTopicList.isEmpty() || normalTopicList.isEmpty()
    }
}