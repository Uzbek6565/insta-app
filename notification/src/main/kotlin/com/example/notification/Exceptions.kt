package com.example.notification

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import java.util.*

sealed class NotificationServiceException(message: String? = null) : RuntimeException(message) {
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

class PostNotFoundException(val id : Long) : NotificationServiceException() {
    override fun errorType() = ErrorCode.POST_NOT_FOUND
}

class LikeNotFoundException(val id : Long) : NotificationServiceException() {
    override fun errorType() = ErrorCode.LIKE_NOT_FOUND
}

class UserNotFoundException(val id : Long) : NotificationServiceException() {
    override fun errorType() = ErrorCode.LIKE_NOT_FOUND
}

class GeneralApiException(val msg: String) : NotificationServiceException() {
    override fun errorType(): ErrorCode = ErrorCode.GENERAL_API_EXCEPTION
}

class FeignErrorException(val code: Int?, val errorMessage: String?) : NotificationServiceException() {
    override fun errorType() = ErrorCode.GENERAL_API_EXCEPTION
}

class NotificationNotFoundException : NotificationServiceException() {
    override fun errorType() = ErrorCode.NOTIFICATION_NOT_FOUND
}