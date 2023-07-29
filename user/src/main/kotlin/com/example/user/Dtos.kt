package com.example.user

import java.util.*

data class BaseMessage(val code: Int, val message: String?)

data class FollowDto(
    var followerId: Long,
    var status :String
)

data class UserCreateDto(
    val username: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val birthDate: Date,
    val bio: String,
    val gender: String,

){
    fun toEntity() = User(username, email, password, phoneNumber, Gender.valueOf(gender), firstName, lastName, birthDate, bio)
}

data class GetOneUserDto(
    val username: String,
    val email: String,
    val phoneNumber: String,
    val gender: Gender,
    val firstName: String?,
    val lastName: String?,
    val birthDate: Date?,
    val bio: String?,
){
    companion object{
        fun toDto(user: User) = user.run { GetOneUserDto(username,email ,phoneNumber, gender, firstName, lastName, birthDate, bio) }
    }
}

data class UpdateUserDto(
    val username: String?,
    val email: String?,
    val password: String?,
    val phoneNumber: String?,
    val firstName: String?,
    val lastName: String?,
    val birthDate: Date?,
    val bio: String?,
    val gender: String?,
)

data class FindUserDto(
    val id:Long
){
    companion object {
        fun toDto(user: User) = user.run { FindUserDto(id!!) }
    }
}