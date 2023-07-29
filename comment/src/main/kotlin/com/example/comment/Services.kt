package com.example.comment

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable


@FeignClient(name = "user")
interface UserService {
    @GetMapping("/internal/exist/{id}")
    fun existsById(@PathVariable id: Long): Boolean

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): UserDto

}

@FeignClient(name = "post")
interface PostService {
    @GetMapping("/internal/exist/{id}")
    fun existsById(@PathVariable id: Long): Boolean

    @GetMapping("{id}")
    fun getById(@PathVariable id: Long): PostDto


}

interface CommentService {
    fun create(dto: CommentCreateDto)
    fun getById(id: Long): GetOneCommentDto
    fun existById(id: Long): Boolean
    fun delete(id: Long)
    fun getAll(pageable: Pageable): Page<GetOneCommentDto>
    fun update(id: Long, commentDto: CommentCreateDto): GetOneCommentDto
}

@Service
class CommentServiceImpl(
    private val repository: CommentRepository,
    private val userService: UserService,
    private val postService: PostService,

    ) : CommentService {
    override fun create(dto: CommentCreateDto) {
        dto.run {
            userService.existsById(userId).runIfFalse { throw UserNotFoundException() }
            postService.existsById(postId).runIfFalse { throw PostNotFoundException() }
            repository.save(toEntity())
        }
    }

    override fun getById(id: Long) = repository.findByIdAndDeletedFalse(id)?.run { GetOneCommentDto.toDto(this) }
        ?: throw CommentNotFoundException()

    override fun existById(id: Long): Boolean {
        return repository.existsByIdAndDeletedFalse(id)
    }

    override fun delete(id: Long) {
        repository.trash(id) ?: throw CommentNotFoundException()
    }

    override fun getAll(pageable: Pageable) =
        repository.findAllNotDeleted(pageable).map { comment -> GetOneCommentDto.toDto(comment) }

    override fun update(id: Long, commentDto: CommentCreateDto): GetOneCommentDto {
        val comment = repository.findByIdAndDeletedFalse(id) ?: throw CommentNotFoundException()

        val editedComment = commentDto.run {
            userId.let {
                userService.existsById(it).runIfFalse { throw UserNotFoundException() }
                comment.userId = it
            }
            postId.let {
                postService.existsById(postId).runIfFalse { throw PostNotFoundException() }
                comment.postId = it
            }
            content.let { comment.content = it }

            repository.save(comment)
        }
        return GetOneCommentDto.toDto(editedComment)
    }


}