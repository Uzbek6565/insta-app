package com.example.notification

import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.jpa.repository.Temporal
import java.util.*
import javax.persistence.*
import javax.persistence.EnumType.STRING

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @CreatedDate @Temporal(TemporalType.TIMESTAMP) var createdDate: Date? = null,
    @LastModifiedDate @Temporal(TemporalType.TIMESTAMP) var modifiedDate: Date? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false
)

@Entity
class Notification(
    @Column(nullable = false) val userId: Long,
    @Enumerated(STRING) val activityType: Activity,
    @Column(nullable = false) val sourceUserId: Long,
    val postId: Long? = null,
    val messageId: Long? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") val isRead: Boolean,

    ) : BaseEntity()