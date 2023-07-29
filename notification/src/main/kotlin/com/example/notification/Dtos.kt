package com.example.notification

import java.util.Date

data class BaseMessage(val code: Int, val message: String?)

data class UserDto(
    val id:Long
)

data class PostDto(
    val id:Long
)

data class MessageDto(
    val id:Long
)

data class UserPostNotificationDto(
    val firstName: String,
    val lastName: String,
    val content: String,
    val createdDate: Date
)

data class NotificationDto(
    val userId: Long,
    val activityType: String,
    val sourceUserId: Long,
    val postId: Long?,
    val messageId: Long?,
    val isRead: Boolean,
){
    fun toEntity(dto: NotificationDto) = dto.run { Notification(userId, Activity.valueOf(activityType), sourceUserId, postId, messageId, isRead) }
}

data class GetOneNotificationDto(
    val userId: Long,
    val activityType: Activity,
    val sourceUserId: Long,
    val postId: Long?,
    val messageId: Long?,
){
    companion object{
        fun toDto(notification: Notification) = notification.run { GetOneNotificationDto(userId, activityType, sourceUserId, postId, messageId) }
    }
}


