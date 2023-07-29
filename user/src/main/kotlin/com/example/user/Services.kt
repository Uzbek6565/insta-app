package com.example.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


interface UserService {
    fun create(dto: UserCreateDto)
    fun getById(id: Long): GetOneUserDto
    fun existById(id: Long): Boolean
    fun findById(id: Long): FindUserDto?
    fun delete(id: Long)
    fun getAll(pageable: Pageable): Page<GetOneUserDto>
    fun update(id: Long, userDto: UpdateUserDto): GetOneUserDto
}

interface FollowService {
    fun followAndUnfollow(userId: Long, dto: FollowDto)
    fun delete(userId: Long)
    fun getAllFollowings(id: Long): List<FollowDto>
    fun getAllFollowers(id: Long): List<FollowDto>
    fun getOneFollowing(userId: Long, followingId: Long): User
    fun getOneFollower(userId: Long, followerId: Long): User
}

@Service
class FollowServiceImpl(
    private val followRepository: FollowRepository,
    private val userRepository: UserRepository,
) : FollowService {
    override fun followAndUnfollow(userId: Long, dto: FollowDto) {
        val user = userRepository.findByIdAndDeletedFalse(userId) ?: throw UserNotFoundException()
        dto.run {
            if(followerId == userId) throw UsersTheSameException()
            val follower = userRepository.findByIdAndDeletedFalse(followerId) ?: throw UserNotFoundException()
            val followingIdAndFollowerId = followRepository.findByFollowingIdAndFollowerIdAndDeletedFalse(userId, followerId)
            if (followingIdAndFollowerId != null) {
                followingIdAndFollowerId.status = FollowStatus.valueOf(status)
                followRepository.save(followingIdAndFollowerId)
            } else {
                val follow = Follow(user, follower, FollowStatus.valueOf(status))
                followRepository.save(follow)
            }
        }
    }


    override fun delete(userId: Long) {
        followRepository.findAllByFollowerIdAndStatusAndDeletedFalse(userId, FollowStatus.FOLLOWING).forEach { follow ->
            follow.id?.let { followRepository.trash(it) }
        }

        followRepository.findAllByFollowingIdAndStatusAndDeletedFalse(userId, FollowStatus.FOLLOWING).forEach { follow ->
            follow.id?.let { followRepository.trash(it) }
        }
    }

    override fun getAllFollowings(id: Long) =
        followRepository.findAllByFollowerIdAndStatusAndDeletedFalse(id, FollowStatus.FOLLOWING).map { follow ->
            follow.following.let { FollowDto(it.id!!, follow.status.toString()) }
        }

    override fun getAllFollowers(id: Long) =
        followRepository.findAllByFollowingIdAndStatusAndDeletedFalse(id, FollowStatus.FOLLOWING).map { follow ->
            follow.follower.let { FollowDto(it.id!!, follow.status.toString()) }
        }

    override fun getOneFollowing(userId: Long, followingId: Long): User {
        val follow = followRepository.findByFollowingIdAndFollowerIdAndDeletedFalseAndStatus(followingId, userId, FollowStatus.FOLLOWING) ?: throw UserNotFoundException()
        return follow.following
    }

    override fun getOneFollower(userId: Long, followerId: Long): User {
        val follow = followRepository.findByFollowingIdAndFollowerIdAndDeletedFalseAndStatus(userId, followerId, FollowStatus.FOLLOWING) ?: throw UserNotFoundException()
        return follow.follower
    }

}

@Service
class UserServiceImpl(
    private val repository: UserRepository
) : UserService {
    override fun create(dto: UserCreateDto) {
        repository.save(dto.toEntity())
    }

    override fun getById(id: Long) = repository.findByIdAndDeletedFalse(id)?.run { GetOneUserDto.toDto(this) }
        ?: throw UserNotFoundException()

    override fun existById(id: Long): Boolean {
        return repository.existsByIdAndDeletedFalse(id)
    }

    override fun findById(id: Long): FindUserDto? {
        val user = repository.findByIdAndDeletedFalse(id)
        if (user != null)
            return FindUserDto.toDto(user)
        return user
    }

    override fun delete(id: Long) {
        repository.trash(id) ?: throw UserNotFoundException()
    }

    override fun getAll(pageable: Pageable): Page<GetOneUserDto> {
        return repository.findAllNotDeleted(pageable).map { user ->
            GetOneUserDto.toDto(user)
        }

    }

    override fun update(id: Long, userDto: UpdateUserDto): GetOneUserDto {
        val user = repository.findByIdAndDeletedFalse(id) ?: throw UserNotFoundException()
        val editedUser = userDto.run {
            username?.let {
                repository.existsByUsername(it).runIfTrue { throw UsernameExistsException() }
                user.username = it
            }
            email?.let {
                repository.existsByEmail(it).runIfTrue { throw EmailExistsException() }
                user.email = it
            }
            phoneNumber?.let {
                repository.existsByPhoneNumber(it).runIfTrue { throw PhoneNumberExistsException() }
                user.phoneNumber = it
            }
            password?.let { user.password = it }
            firstName?.let { user.firstName = it }
            lastName?.let { user.lastName = it }
            birthDate?.let { user.birthDate = it }
            bio?.let { user.bio = it }
            gender?.let { user.gender = Gender.valueOf(it) }

            repository.save(user)
        }
        return GetOneUserDto.toDto(editedUser)
    }

}