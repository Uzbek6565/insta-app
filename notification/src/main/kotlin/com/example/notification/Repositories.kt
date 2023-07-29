package com.example.notification

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@NoRepositoryBean
interface BaseRepository<T : BaseEntity> : JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    fun findByIdAndDeletedFalse(id: Long): T?
    fun trash(id: Long): T?
    fun trashList(ids: List<Long>): List<T?>
    fun findAllNotDeleted(): List<T>
    fun findAllNotDeleted(pageable: Pageable): Page<T>
}

class BaseRepositoryImpl<T : BaseEntity>(
    entityInformation: JpaEntityInformation<T, Long>, entityManager: EntityManager,
) : SimpleJpaRepository<T, Long>(entityInformation, entityManager), BaseRepository<T> {

    val isNotDeletedSpecification = Specification<T> { root, _, cb -> cb.equal(root.get<Boolean>("deleted"), false) }

    override fun findByIdAndDeletedFalse(id: Long) = findByIdOrNull(id)?.run { if (deleted) null else this }

    override fun findAllNotDeleted(pageable: Pageable): Page<T> = findAll(isNotDeletedSpecification, pageable)

    override fun findAllNotDeleted(): List<T> = findAll(isNotDeletedSpecification)

    @Transactional
    override fun trash(id: Long): T? = findByIdOrNull(id)?.run {
        deleted = true
        save(this)
    }

    @Transactional
    override fun trashList(ids: List<Long>): List<T?> = ids.map { trash(it) }
}

interface NotificationRepository : BaseRepository<Notification> {
    fun findAllByUserIdAndActivityTypeAndDeletedFalse(userId: Long, activity: Activity, pageable: Pageable): Page<NotificationDto>
    fun findBySourceUserIdAndPostIdAndDeletedFalse(userId: Long, postId: Long): Notification?

    @Query("SELECT u.first_name, u.last_name, p.content, n.created_date" +
            "    from user as u" +
            "    join notification as n ON u.id = n.source_user_id" +
            "    join post as p ON n.post_id = p.id " +
            "WHERE n.is_read = false " +
            "   and n.user_id = :user_id " +
            "   and n.deleted = false", nativeQuery = true)
    fun getAllByUserIdAndActivityTypeAndDeletedFalse(@Param("user_id") userId: Long, pageable: Pageable ): Page<UserPostNotificationDto>
}

