package com.kdays.android.logic.model.draft.drafts

data class DraftsResponse(
    val code: Int,
    val data: List<Draft>,
    val msg: String?
)