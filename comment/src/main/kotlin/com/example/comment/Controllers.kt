package com.example.comment

import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@ControllerAdvice
class ExceptionHandlers(
    private val errorMessageSource: ResourceBundleMessageSource
) {
    @ExceptionHandler(CommentServiceException::class)
    fun handleException(exception: CommentServiceException): ResponseEntity<*> {
        return when (exception) {
            is CommentNotFoundException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, emptyArray<Any>())
            )

            is PostNotFoundException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, emptyArray<Any>())
            )

            is UserNotFoundException -> ResponseEntity.badRequest().body(
                exception.getErrorMessage(errorMessageSource, emptyArray<Any>())
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
class CommentController(private val service: CommentService) {
    @PostMapping
    fun create(@RequestBody dto: CommentCreateDto) = service.create(dto)

    @GetMapping("{id}")
    fun getById(@PathVariable id: Long) = service.getById(id)

    @GetMapping
    fun getAll(pageable: Pageable) = service.getAll(pageable)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody commentDto: CommentCreateDto) = service.update(id, commentDto)

    @DeleteMapping("{id}")
    fun deleteById(@PathVariable id: Long) = service.delete(id)


}

@RestController
@RequestMapping("internal")
class CommentInternalController(private val service: CommentService) {
    @GetMapping("exists/{id}")
    fun existById(@PathVariable id: Long) = service.existById(id)
}