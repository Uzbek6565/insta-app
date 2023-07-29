package com.example.user

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import java.util.*


sealed class UserServiceException(message: String? = null) : RuntimeException(message) {
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

class UserNotFoundException : UserServiceException() {
    override fun errorType() = ErrorCode.USER_NOT_FOUND
}

class UsersTheSameException : UserServiceException() {
    override fun errorType() = ErrorCode.USERS_THE_SAME
}

class UsernameExistsException : UserServiceException() {
    override fun errorType() = ErrorCode.USERNAME_EXISTS
}

class EmailExistsException : UserServiceException() {
    override fun errorType() = ErrorCode.EMAIL_EXISTS
}

class PhoneNumberExistsException : UserServiceException() {
    override fun errorType() = ErrorCode.PHONE_NUMBER_EXISTS
}

