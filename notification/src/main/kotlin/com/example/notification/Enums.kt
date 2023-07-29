package com.example.notification

enum class ErrorCode(val code: Int) {
    POST_NOT_FOUND(300),
    LIKE_NOT_FOUND(301),
    USER_NOT_FOUND(302),
    GENERAL_API_EXCEPTION(303),
    NOTIFICATION_NOT_FOUND(304)


}

enum class Activity{
    LIKE,
    COMMENT,
    POST,
    DIRECT,
    FOLLOW,
    MENTION,
    REPLY
}