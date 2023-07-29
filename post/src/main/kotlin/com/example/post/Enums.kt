package com.example.post

enum class ErrorCode(val code: Int) {
    POST_NOT_FOUND(200),
    LIKE_NOT_FOUND(201),
    USER_NOT_FOUND(202),
    GENERAL_API_EXCEPTION(203),


}
