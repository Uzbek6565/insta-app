package com.example.comment

data class BaseMessage(val code: Int, val message: String?)

data class CommentCreateDto(
    val postId: Long,
    val userId: Long,
    var content: String,
){
    fun toEntity() = Comment(postId, userId, content)
}

data class GetOneCommentDto(
    val postId: Long,
    val userId: Long,
    var content: String,
){
    companion object{
        fun toDto(comment: Comment) = comment.run { GetOneCommentDto(postId, userId, content) }
    }
}

data class UserDto(
    val id:Long
)

data class PostDto(
    val id:Long
)