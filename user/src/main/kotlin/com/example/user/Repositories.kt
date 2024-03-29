package com.example.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
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

interface UserRepository : BaseRepository<User> {
    fun existsByIdAndDeletedFalse(id: Long): Boolean
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun existsByPhoneNumber(phoneNumber: String): Boolean
}

interface FollowRepository : BaseRepository<Follow> {
//    @Query("SELECT * FROM Follow f WHERE f.following.id = ?1 and f.status = ?2", nativeQuery = true)
//    fun getAllFollowerByFollowingId(id: Long, status: String): List<Follow>
//
//    @Query("SELECT * FROM Follow f WHERE f.follower.id = ?1 and f.status = ?2", nativeQuery = true)
//    fun getAllFollowingByFollowerId(id: Long, status: String): List<Follow>

    fun findByFollowingIdAndFollowerIdAndDeletedFalse(userId:Long, followerId:Long):Follow?
    fun findByFollowingIdAndFollowerIdAndDeletedFalseAndStatus(userId:Long, followerId:Long, status: FollowStatus):Follow?

    fun findAllByFollowingIdAndStatusAndDeletedFalse(followingId: Long, status: FollowStatus): List<Follow>
    fun findAllByFollowerIdAndStatusAndDeletedFalse(followerId: Long, status: FollowStatus): List<Follow>


}