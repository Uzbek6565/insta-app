package com.example.post

data class BaseMessage(val code: Int, val message: String?)

data class PostDto(
    val userId: Long,
    val content: String,
){
    fun toEntity() = Post(userId, content)
}

data class FollowDto(
    var followerId: Long,
    var status :String
)

data class NotificationDto(
    val userId: Long,
    val activityType: String,
    val sourceUserId: Long,
    val postId: Long?,
    val messageId: Long?,
    val isRead: Boolean,
)


data class GetOnePostDto(
    val userId: Long,
    val content: String,
    val view: Long?,
    val likes: MutableList<GetLikeDto> = mutableListOf(),
){
    companion object{
        fun toDto(post: Post) = post.run { GetOnePostDto(userId, content, view) }
    }
}

data class UserDto(
    val id:Long
)

data class LikeDto(
    val userId: Long,
    val post: PostDto
){
    fun toEntity() = Like(userId, post.toEntity())
}

data class LikePostDto(
    val userId: Long,
    val postId: Long,
)

data class GetLikeDto(
    val userId: Long,
){
    companion object{
        fun toDto(like: Like) = like.run { GetLikeDto(userId) }
    }
}

