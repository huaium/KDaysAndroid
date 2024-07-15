package com.kdays.android

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.kdays.android.logic.Repository
import com.kdays.android.logic.model.forum.OuterForum
import com.kdays.android.logic.model.toptopic.Topic

class MainViewModel : ViewModel() {

    enum class CurrentFragment {
        TopTopic, User, Forum
    }

    private var currentFragment = CurrentFragment.Forum

    fun setCurrentFragment(current: CurrentFragment) {
        currentFragment = current
    }

    // For TopTopicFragment
    private val getTopTopicListLiveData = MutableLiveData<String>()

    val topTopicsList = mutableListOf<Topic>()

    val topPostsList = mutableListOf<Topic>()

    val topTopicLiveData = getTopTopicListLiveData.switchMap { _ ->
        Repository.getTopTopic()
    }

    private fun getTopTopicList() {
        getTopTopicListLiveData.value = getTopTopicListLiveData.value
    }

    private fun refreshTopTopic() {
        getTopTopicList()
    }

    fun noTopTopicCache(): Boolean {
        return topTopicsList.isEmpty()
    }

    // For ForumFragment
    private val getOuterForumListLiveData = MutableLiveData<String>()

    val outerForumList = mutableListOf<OuterForum>()

    val outerForumListLiveData =
        getOuterForumListLiveData.switchMap { bbsToken ->
            Repository.getForumList(bbsToken)
        }

    private fun getOuterForumList(bbsToken: String) {
        getOuterForumListLiveData.value = bbsToken
    }

    private fun refreshForum() {
        if (isTokenSaved()) {
            // Now we can assert that token is not empty
            val accessToken = getSavedToken()!!
            getOuterForumList(accessToken)
        }
    }

    fun noForumCache(): Boolean {
        return outerForumList.isEmpty()
    }

    // For UserFragment
    private val ucTokenLiveData = MutableLiveData<String>()

    val userInfoLiveData = ucTokenLiveData.switchMap { bbsToken ->
        Repository.getUserInfo(bbsToken)
    }

    private fun getUserInfo(bbsToken: String) {
        ucTokenLiveData.value = bbsToken
    }

    // Storage
    // TokenDao
    private fun getSavedToken() = Repository.getSavedToken()

    fun isTokenSaved() = Repository.isTokenSaved()

    // UserAvatarDao
    fun saveUserAvatar(userAvatar: String) = Repository.saveUserAvatar(userAvatar)

    fun getSavedUserAvatar() = Repository.getSavedUserAvatar()

    fun isUserAvatarSaved() = Repository.isUserAvatarSaved()

    // UserNameDao
    fun saveUserName(userName: String) = Repository.saveUserName(userName)

    fun getSavedUserName() = Repository.getSavedUserName()

    fun isUserNameSaved() = Repository.isUserNameSaved()

    // UidDao
    fun saveUid(uid: String) = Repository.saveUid(uid)

    fun getSavedUid() = Repository.getSavedUid()

    fun isUidSaved() = Repository.isUidSaved()

    private fun refreshUser() {
        if (isTokenSaved()) {
            val accessToken = getSavedToken()!!
            getUserInfo(accessToken)
        }
    }

    fun refresh() {
        if (isTokenSaved()) {
            when (currentFragment) {
                CurrentFragment.TopTopic -> refreshTopTopic()
                CurrentFragment.Forum -> refreshForum()
                CurrentFragment.User -> refreshUser()
            }
        }
    }
}