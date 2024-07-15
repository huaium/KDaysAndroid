package com.kdays.android.logic.model.search

data class SearchData(
    val items: List<Topic>,
    val pages: Pages,
    val parameters: Parameters
)