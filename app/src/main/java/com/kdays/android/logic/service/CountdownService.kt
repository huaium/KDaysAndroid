package com.kdays.android.logic.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import com.kdays.android.BaseActivity
import com.kdays.android.logic.Repository
import com.kdays.android.utils.TimeUtils

class CountdownService : Service() {

    companion object {
        private lateinit var startActivity: BaseActivity

        fun actionStart(activity: BaseActivity) {
            startActivity = activity
            val intent = Intent(activity, CountdownService::class.java)
            activity.startService(intent)
        }

        fun actionStop(context: Context) {
            val intent = Intent(context, CountdownService::class.java)
            context.stopService(intent)
        }
    }

    private var countDownTimer: CountDownTimer? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Repository.isTokenExpiredSaved()) {
            val tokenExpired = Repository.getSavedTokenExpired()

            startTokenExpiredTimer(tokenExpired)
        } else {
            startAutoLogin()
            stopSelf()
        }

        return START_STICKY
    }

    private fun startTokenExpiredTimer(tokenExpired: Long) {
        countDownTimer?.cancel()

        val currentTimeSec = TimeUtils.getCurrentTimeSec()
        val timeRemainingMillis = (tokenExpired - currentTimeSec) * 1000

        if (timeRemainingMillis > 0) {
            countDownTimer = object : CountDownTimer(timeRemainingMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    clearAndStart()
                }
            }

            countDownTimer?.start()
        } else {
            clearAndStart()
        }
    }

    private fun clearAndStart() {
        clearLocalToken()
        startAutoLogin()
    }

    private fun clearLocalToken() {
        Repository.removeSavedToken()
        Repository.removeSavedTokenExpired()
    }

    private fun startAutoLogin() {
        startActivity.autoLogin()
    }

    override fun onDestroy() {
        countDownTimer?.cancel() // make sure of stopping counting down onDestroy
        super.onDestroy()
    }
}
