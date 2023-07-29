package com.example.user

import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@ControllerAdvice
class ExceptionHandlers(
    private val errorMessageSource: ResourceBundleMessageSource
) {
    @ExceptionHandler(UserServiceException::class)
    fun handleException(exception: UserServiceException): ResponseEntity<*> {
        return when (exception) {
            is UserNotFoundException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, emptyArray<Any>())
            )

            is EmailExistsException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, emptyArray<Any>())
            )

            is PhoneNumberExistsException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, emptyArray<Any>())
            )

            is UsernameExistsException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, emptyArray<Any>())
            )

            is UsersTheSameException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, emptyArray<Any>())
            )
        }
    }
}

@RestController
class UserController(private val service: UserService) {
    @PostMapping
    fun create(@RequestBody dto: UserCreateDto) = service.create(dto)

    @GetMapping
    fun getAll(pageable: Pageable) = service.getAll(pageable)

    @GetMapping("{id}")
    fun getById(@PathVariable id: Long) = service.getById(id)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody userDto: UpdateUserDto) = service.update(id, userDto)

    @DeleteMapping("{id}")
    fun deleteBYId(@PathVariable id: Long) = service.delete(id)
}

@RestController
@RequestMapping("follow")
class FollowController(
    private val service: FollowService
) {
    @PostMapping("{userId}")
    fun followAndUnfollow(@PathVariable userId:Long, @RequestBody dto: FollowDto) = service.followAndUnfollow(userId, dto)

    @GetMapping("followings/{id}")
    fun getAllFollowings(@PathVariable id: Long) = service.getAllFollowings(id)

    @GetMapping("followers/{id}")
    fun getAllFollowers(@PathVariable id: Long) = service.getAllFollowers(id)

    @GetMapping("{userId}/follower/{followerId}")
    fun getOneFollower(@PathVariable userId: Long,@PathVariable followerId: Long) = service.getOneFollower(userId, followerId)

    @GetMapping("{userId}/following/{followingId}")
    fun getOneFollowing(@PathVariable userId: Long,@PathVariable followingId: Long) = service.getOneFollowing(userId, followingId)

    @DeleteMapping("{id}")
    fun deleteById(@PathVariable id: Long) = service.delete(id)
}

@RestController
@RequestMapping("internal")
class UserInternalController(private val service: UserService) {
    @GetMapping("exists/{id}")
    fun existById(@PathVariable id: Long) = service.existById(id)

    @GetMapping("find/{id}")
    fun findById(@PathVariable id: Long) = service.findById(id)

}