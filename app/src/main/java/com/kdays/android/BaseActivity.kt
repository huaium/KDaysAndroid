package com.kdays.android

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kdays.android.BuildConfig.UC_ADDRESS
import com.kdays.android.ext.StringExts.openUrl
import com.kdays.android.ext.StringExts.showToast
import com.kdays.android.logic.dao.TracingDao
import com.kdays.android.logic.exception.UserCausedException
import com.kdays.android.logic.exception.UserCausedException.BbsErrorCode.AVATAR_NOT_SET
import com.kdays.android.logic.exception.UserCausedException.BbsErrorCode.BBS_ACCOUNT_NOT_CREATED
import com.kdays.android.logic.exception.UserCausedException.UcErrorCode.UNAUTHORIZED
import com.kdays.android.logic.service.CountdownService
import com.kdays.android.utils.NetworkUtils.handleNetwork


open class BaseActivity : AppCompatActivity() {

    private val viewModel: BaseViewModel by viewModels()
    private lateinit var dialog: AlertDialog

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // lock orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (TracingDao.isTracing()) {
            "记录调用日志中，性能会受到影响".showToast()
        }

        CountdownService.actionStart(this)

        dialog = MaterialAlertDialogBuilder(this)
            .setTitle("登录")
            .setView(R.layout.dialog_login)
            .setCancelable(false)
            .create()

        viewModel.ucAccessTokenLiveData.observe(this) { result ->
            val ucData = result.getOrNull()
            if (ucData != null) {
                viewModel.bbsLogin(ucData)
            } else {
                val exception = result.exceptionOrNull()
                if (exception is UserCausedException) {
                    when (exception.getErrorCode()) {
                        UNAUTHORIZED -> {
                            "未授权，请授权后继续".showToast()
                            val authorizeUrl = exception.message
                            authorizeUrl?.openUrl(this)
                        }

                        else -> {
                            exception.message?.showToast()
                            exception.printStackTrace()
                        }
                    }

                } else {
                    handleNetwork {
                        "未知底层原因，未能获取UC Token".showToast()
                        exception?.printStackTrace()
                    }
                }
                dialog.dismiss()
            }
        }

        viewModel.bbsAccessTokenLiveData.observe(this) { result ->
            val triple = result.getOrNull()
            if (triple != null) {
                triple.let { (tokenPair, username, password) ->
                    val accessToken = tokenPair.accessToken
                    val tokenExpired = tokenPair.tokenExpired

                    viewModel.saveToken(accessToken)
                    viewModel.saveTokenExpired(tokenExpired)
                    viewModel.saveInput(username)
                    viewModel.savePassword(password)
                }
            } else {
                val exception = result.exceptionOrNull()
                if (exception is UserCausedException) {
                    when (exception.getErrorCode()) {
                        AVATAR_NOT_SET -> {
                            "未设置头像，请设置后继续".showToast()
                            val url = "$UC_ADDRESS/user/avatar"
                            url.openUrl(this)
                        }

                        BBS_ACCOUNT_NOT_CREATED -> {
                            "未创建论坛账户，请登录⼀次论坛后继续".showToast()
                            val url = "$UC_ADDRESS/sso/login/?client_id=kd.bbs"
                            url.openUrl(this)
                        }

                        else -> {
                            exception.message?.showToast()
                            exception.printStackTrace()
                        }
                    }
                } else {
                    handleNetwork {
                        "未知底层原因，未能获取BBS Token".showToast()
                        exception?.printStackTrace()
                    }
                }
            }
            dialog.dismiss()
            viewModel.setLoginSuccess()
        }

        viewModel.loginSuccessData.observe(this) { isSuccess ->
            if (isSuccess) {
                onLoginSuccess()
            }
        }
    }

    fun login(username: String, password: String) {
        dialog.show()
        viewModel.ucLogin(username, password)
    }

    fun autoLogin() {
        if (viewModel.isInputSaved() && viewModel.isPasswordSaved()) {
            val username = viewModel.getSavedInput()!!
            val password = viewModel.getSavedPassword()!!

            login(username, password)
        }
    }

    protected open fun onLoginSuccess() {}
}