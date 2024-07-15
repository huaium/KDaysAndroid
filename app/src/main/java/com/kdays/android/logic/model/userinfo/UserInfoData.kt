package com.kdays.android.logic.model.userinfo

data class UserInfoData(
    val uid: String,
    val nick: String,
    val avatar: String,
    val fullAvatar: String,
    val notify: Int,
    val email: String,
    val phone: String
)