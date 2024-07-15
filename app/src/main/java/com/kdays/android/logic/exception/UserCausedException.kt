package com.kdays.android.logic.exception

class UserCausedException(
    message: String?,
    private val errorCode: Int,
) : RuntimeException(message ?: "msg is null") {

    object UcErrorCode {
        const val UNAUTHORIZED = -7
    }

    object BbsErrorCode {
        const val AVATAR_NOT_SET = -2
        const val BBS_ACCOUNT_NOT_CREATED = -3
    }

    fun getErrorCode() = errorCode
}