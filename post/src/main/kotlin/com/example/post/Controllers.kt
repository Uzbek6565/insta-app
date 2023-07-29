package com.example.post

import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@ControllerAdvice
class ExceptionHandlers(
    private val errorMessageSource: ResourceBundleMessageSource
) {
    @ExceptionHandler(PostServiceException::class)
    fun handleException(exception: PostServiceException): ResponseEntity<*> {
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

        }
    }
}

@RestController
class PostController(private val service: PostService) {
    @PostMapping
    fun create(@RequestBody dto: PostDto) = service.create(dto)

    @GetMapping
    fun getAll(pageable: Pageable) = service.getAll(pageable)

    @GetMapping("{id}")
    fun getById(@PathVariable id: Long) = service.getById(id)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: PostDto) = service.update(id, dto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}

@RestController
@RequestMapping("internal")
class PostInternalController(private val service: PostService) {
    @GetMapping("exists/{id}")
    fun existById(@PathVariable id: Long) = service.existById(id)
}