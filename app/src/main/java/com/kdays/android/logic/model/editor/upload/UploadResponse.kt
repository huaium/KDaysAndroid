package com.kdays.android.logic.model.editor.upload

data class UploadResponse(
    val code: Int,
    val data: UploadData,
    val msg: String?
)