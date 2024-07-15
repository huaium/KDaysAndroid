package com.kdays.android.logic.model.usertopics

data class UserTopicsResponse(
    val code: Int,
    val data: UserTopicsData,
    val msg: String?,
)