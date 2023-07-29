package com.example.comment

enum class Gender{
    MALE,
    FEMALE
}

enum class ErrorCode(val code: Int) {
    COMMENT_NOT_FOUND(400),
    USER_NOT_FOUND(401),
    POST_NOT_FOUND(402),
    GENERAL_API_EXCEPTION(403),
}