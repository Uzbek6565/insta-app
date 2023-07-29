package com.example.post

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.transaction.annotation.Transactional

@FeignClient(name = "user")
interface UserService {
    @GetMapping("internal/exists/{id}")
    fun existById(@PathVariable id: Long): Boolean

    @GetMapping("internal/find/{id}")
    fun findById(@PathVariable id: Long): UserDto?

    @GetMapping("follow/followings/{id}")
    fun getAllFollowings(@PathVariable id: Long) : List<FollowDto>

}

@FeignClient(name = "notification")
interface NotificationService{
    @PostMapping
    fun create(@RequestBody notificationDto : NotificationDto)
}

interface PostService {
    fun create(dto: PostDto)
    fun getById(id: Long): GetOnePostDto
    fun existById(id: Long): Boolean
    fun update(id: Long, dto: PostDto): GetOnePostDto
    fun delete(id: Long)
    fun getAll(pageable: Pageable): Page<GetOnePostDto>
}

interface LikeService {
    fun create(dto: LikePostDto)
    fun getById(id: Long): GetLikeDto
    fun existById(id: Long): Boolean
    fun update(id: Long, dto: LikeDto)
    fun delete(id: Long)
}

@Service
class LikeServiceImpl(
    private val likeRepository: LikeRepository,
    private val postRepository: PostRepository,
    private val userService: UserService
) : LikeService {
    @Transactional
    override fun create(dto: LikePostDto) {
        dto.run {
            val user = userId.run { userService.findById(this) ?: throw UserNotFoundException(this) }
            val post =
                postId.run { postRepository.findByIdAndDeletedFalse(this) ?: throw PostNotFoundException(this) }
            val like = likeRepository.findByUserIdAndPostId(user.id, post.id!!)
            like.deleted = !like.deleted
            likeRepository.save(like)
        }
    }

    override fun getById(id: Long): GetLikeDto {
        val like = likeRepository.findByIdAndDeletedFalse(id) ?: throw LikeNotFoundException(id)
        return GetLikeDto.toDto(like)
    }

    override fun existById(id: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun update(id: Long, dto: LikeDto) {

    }

    override fun delete(id: Long) {
        likeRepository.trash(id) ?: throw LikeNotFoundException(id)
    }

}

@Service
class PostServiceImpl(
    private val repository: PostRepository,
    private val userService: UserService,
    private val notificationService: NotificationService
) : PostService {
    @Transactional
    override fun create(dto: PostDto) {
        dto.run { userService.existById(userId).runIfFalse { throw UserNotFoundException(userId) } }
        val savedPost = repository.save(dto.toEntity())

        val followings = userService.getAllFollowings(savedPost.userId)
        followings.forEach {
            followDto -> val notificationDto =
            NotificationDto(followDto.followerId, "POST",savedPost.userId , savedPost.id, null, false)
            notificationService.create(notificationDto)
        }

    }

    override fun getById(id: Long) = repository.findByIdAndDeletedFalse(id)?.run { GetOnePostDto.toDto(this) }
        ?: throw PostNotFoundException(id)

    override fun existById(id: Long): Boolean {
        return repository.existsByIdAndDeletedFalse(id)
    }

    override fun update(id: Long, dto: PostDto): GetOnePostDto {
        val post = repository.findByIdAndDeletedFalse(id) ?: throw PostNotFoundException(id)

        val editedPost = dto.run {
            content.let { post.content = it }
            repository.save(post)
        }
        return GetOnePostDto.toDto(editedPost)
    }

    override fun delete(id: Long) {
        repository.trash(id) ?: throw PostNotFoundException(id)
    }

    override fun getAll(pageable: Pageable) =
        repository.findAllNotDeleted(pageable).map { post -> GetOnePostDto.toDto(post) }


}