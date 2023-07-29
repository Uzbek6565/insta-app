package com.example.notification

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaAuditing
@EnableFeignClients
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl::class)
@SpringBootApplication
class NotificationApplication

fun main(args: Array<String>) {
    runApplication<NotificationApplication>(*args)
}
