package com.kdays.android.logic.model.userinfo

data class UserInfoResponse(
    val code: Int,
    val data: UserInfoData?,
    val msg: String?
)