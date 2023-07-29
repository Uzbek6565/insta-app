package com.example.user

enum class Gender{
    MALE,
    FEMALE
}

enum class ErrorCode(val code: Int) {
    USER_NOT_FOUND(100),
    USERNAME_EXISTS(101),
    EMAIL_EXISTS(102),
    PHONE_NUMBER_EXISTS(103),
    USERS_THE_SAME(104),
}

enum class FollowStatus{
    FOLLOWING,
    REQUESTED,
    REJECTED,
    NOT_FOLLOWING
}