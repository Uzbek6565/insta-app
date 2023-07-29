package com.example.notification

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "user")
interface UserService {
    @GetMapping("internal/exists/{id}")
    fun existById(@PathVariable id: Long): Boolean

    @GetMapping("internal/find/{id}")
    fun findById(@PathVariable id: Long): UserDto?

}

@FeignClient(name = "post")
interface PostService {
    @GetMapping("internal/exists/{id}")
    fun existById(@PathVariable id: Long): Boolean

    @GetMapping("internal/find/{id}")
    fun findById(@PathVariable id: Long): PostDto?

}

interface NotificationService {
    fun create(dto: NotificationDto)
    fun getById(id: Long): GetOneNotificationDto
    fun existById(id: Long): Boolean
    fun update(id: Long, dto: NotificationDto)
    fun delete(userId: Long, postId: Long)
    fun getAllPostsByUserId(id: Long, pageable: Pageable): Page<UserPostNotificationDto>

    //    fun getAllPostsByUserId(id: Long, pageable: Pageable): Page<NotificationDto>
    fun getAllMessagesByUserId(id: Long, pageable: Pageable): Page<NotificationDto>
    fun getAll(pageable: Pageable): Page<Notification>
}

@Service
class NotificationServiceImpl(
    private val userService: UserService,
    private val postService: PostService,
    private val notificationRepository: NotificationRepository
) : NotificationService {
    override fun create(dto: NotificationDto) {
        dto.run {
            notificationRepository.save(Notification(userId, Activity.valueOf(activityType), sourceUserId, postId, messageId, isRead))
        }
    }

    override fun getById(id: Long): GetOneNotificationDto {
        val notification = notificationRepository.findByIdAndDeletedFalse(id) ?: throw NotificationNotFoundException()
        return GetOneNotificationDto.toDto(notification)
    }

    override fun existById(id: Long): Boolean {
        return true
    }

    override fun update(id: Long, dto: NotificationDto) {
        TODO("Not yet implemented")
    }

    override fun delete(userId: Long, postId: Long) {
        val notification =
            notificationRepository.findBySourceUserIdAndPostIdAndDeletedFalse(userId, postId)
                ?: throw NotificationNotFoundException()
        notificationRepository.trash(notification.id!!)
    }

    override fun getAllPostsByUserId(id: Long, pageable: Pageable) =
        notificationRepository.getAllByUserIdAndActivityTypeAndDeletedFalse(id, pageable)

//    override fun getAllPostsByUserId(id: Long, pageable: Pageable) =
//        notificationRepository.findAllByUserIdAndActivityTypeAndDeletedFalse(id, Activity.POST, pageable)

    override fun getAllMessagesByUserId(id: Long, pageable: Pageable) =
        notificationRepository.findAllByUserIdAndActivityTypeAndDeletedFalse(id, Activity.DIRECT, pageable)

    override fun getAll(pageable: Pageable) = notificationRepository.findAllNotDeleted(pageable)


}