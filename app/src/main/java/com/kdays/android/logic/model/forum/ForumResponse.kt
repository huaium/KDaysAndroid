package com.kdays.android.logic.model.forum

data class ForumResponse(
    val code: Int,
    val data: ForumData,
    val msg: String?
)