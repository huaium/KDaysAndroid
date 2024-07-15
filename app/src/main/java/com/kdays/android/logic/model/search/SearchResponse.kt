package com.kdays.android.logic.model.search

data class SearchResponse(
    val code: Int,
    val data: SearchData,
    val msg: String?
)