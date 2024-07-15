package com.kdays.android.logic.model.editor.upload

data class UploadRequest(
    val body: ByteArray,
    val token: String,
    val fid: String,
    val uploadName: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UploadRequest

        if (!body.contentEquals(other.body)) return false
        if (token != other.token) return false
        if (fid != other.fid) return false
        return uploadName == other.uploadName
    }

    override fun hashCode(): Int {
        var result = body.contentHashCode()
        result = 31 * result + token.hashCode()
        result = 31 * result + fid.hashCode()
        result = 31 * result + uploadName.hashCode()
        return result
    }
}
