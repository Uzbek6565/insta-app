package com.example.notification

import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@ControllerAdvice
class ExceptionHandlers(
    private val errorMessageSource: ResourceBundleMessageSource
) {
    @ExceptionHandler(NotificationServiceException::class)
    fun handleException(exception: NotificationServiceException): ResponseEntity<*> {
        return when (exception) {
            is PostNotFoundException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, emptyArray<Any>())
            )

            is UserNotFoundException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource)
            )

            is LikeNotFoundException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource)
            )

            is FeignErrorException -> ResponseEntity.badRequest().body(
                BaseMessage(exception.code!!, exception.errorMessage)
            )

            is GeneralApiException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, exception.msg)
            )

            is NotificationNotFoundException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource)
            )
        }
    }
}


@RestController
class NotificationController(private val service: NotificationService) {
    @PostMapping
    fun create(@RequestBody dto: NotificationDto) = service.create(dto)

    @GetMapping
    fun getAll(pageable: Pageable) = service.getAll(pageable)

    @GetMapping("{id}")
    fun getById(@PathVariable id: Long) = service.getById(id)

    @GetMapping("user/{id}/posts")
    fun getAllPostByUserId(@PathVariable id: Long, pageable: Pageable) = service.getAllPostsByUserId(id, pageable)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: NotificationDto) = service.update(id, dto)

    @DeleteMapping
    fun delete(@RequestParam userId: Long, @RequestParam postId: Long) = service.delete(userId,postId)
}

@RestController
@RequestMapping("internal")
class NotificationInternalController(private val service: NotificationService) {
    @GetMapping("exists/{id}")
    fun existById(@PathVariable id: Long) = service.existById(id)
}