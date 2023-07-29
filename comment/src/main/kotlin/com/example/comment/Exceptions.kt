package com.example.comment

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import java.util.*


sealed class CommentServiceException(message: String? = null) : RuntimeException(message) {
    abstract fun errorType(): ErrorCode

    fun getErrorMessage(errorMessageSource: ResourceBundleMessageSource, vararg array: Any?): BaseMessage {
        return BaseMessage(
            errorType().code,
            errorMessageSource.getMessage(
                errorType().toString(),
                array,
                Locale(LocaleContextHolder.getLocale().language)
            )
        )
    }
}

class CommentNotFoundException : CommentServiceException() {
    override fun errorType() = ErrorCode.COMMENT_NOT_FOUND
}

class UserNotFoundException : CommentServiceException() {
    override fun errorType() = ErrorCode.USER_NOT_FOUND
}

class PostNotFoundException : CommentServiceException() {
    override fun errorType() = ErrorCode.POST_NOT_FOUND
}

class GeneralApiException(val msg: String) : CommentServiceException() {
    override fun errorType(): ErrorCode = ErrorCode.GENERAL_API_EXCEPTION
}

class FeignErrorException(val code: Int?, val errorMessage: String?) : CommentServiceException() {
    override fun errorType() = ErrorCode.GENERAL_API_EXCEPTION
}

