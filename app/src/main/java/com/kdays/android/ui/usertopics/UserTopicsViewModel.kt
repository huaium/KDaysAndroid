package com.kdays.android.ui.usertopics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import com.kdays.android.logic.Repository
import com.kdays.android.logic.model.usertopics.Topic
import kotlin.properties.Delegates

class UserTopicsViewModel(private val uid: String) : ViewModel() {
    @Suppress("UNCHECKED_CAST")
    class Factory(private val uid: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserTopicsViewModel::class.java)) {
                return UserTopicsViewModel(uid) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val getUserTopicsLiveData = MutableLiveData<Triple<String, String, String>>()

    val topicList = mutableListOf<Topic>()

    var pageTotal by Delegates.notNull<Int>()

    val topicListLiveData =
        getUserTopicsLiveData.switchMap { (uid, page, token) ->
            Repository.getUserTopics(uid, page, token)
        }

    fun getUserTopics(page: String) {
        if (isTokenSaved()) {
            val token = getSavedToken()!!
            getUserTopicsLiveData.value = Triple(uid, page, token)
        }
    }

    // Storage
    private fun getSavedToken() = Repository.getSavedToken()

    private fun isTokenSaved() = Repository.isTokenSaved()

    fun noCache(): Boolean {
        return topicList.isEmpty()
    }
}