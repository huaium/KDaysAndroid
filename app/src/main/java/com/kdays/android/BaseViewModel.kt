package com.kdays.android

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.kdays.android.logic.Repository
import com.kdays.android.logic.model.bbslogin.BbsLoginRequest

class BaseViewModel : ViewModel() {

    val loginSuccessData: LiveData<Boolean>
        get() = _loginSuccessData

    private val _loginSuccessData = MutableLiveData<Boolean>()

    private val ucLoginLiveData = MutableLiveData<Pair<String, String>>()

    private val bbsLoginLiveData = MutableLiveData<BbsLoginRequest>()

    val ucAccessTokenLiveData =
        ucLoginLiveData.switchMap { (username, password) ->
            Repository.ucLogin(
                username,
                password
            )
        }

    val bbsAccessTokenLiveData =
        bbsLoginLiveData.switchMap { ucData ->
            Repository.bbsLogin(ucData)
        }

    fun ucLogin(username: String, password: String) {
        ucLoginLiveData.value = Pair(username, password)
    }

    fun bbsLogin(bbsLoginRequest: BbsLoginRequest) {
        bbsLoginLiveData.value = bbsLoginRequest
    }

    fun setLoginSuccess() {
        _loginSuccessData.value = true
    }

    // Storage
    fun saveToken(token: String) = Repository.saveToken(token)

    fun saveTokenExpired(tokenExpired: Long) = Repository.saveTokenExpired(tokenExpired)

    // InputDao
    fun saveInput(input: String) = Repository.saveInput(input)

    fun getSavedInput() = Repository.getSavedInput()

    fun isInputSaved() = Repository.isInputSaved()

    // PasswordDao
    fun savePassword(password: String) = Repository.savePassword(password)

    fun getSavedPassword() = Repository.getSavedPassword()

    fun isPasswordSaved() = Repository.isPasswordSaved()
}