package com.example.post

import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.jpa.repository.Temporal
import java.util.*
import javax.persistence.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @CreatedDate @Temporal(TemporalType.TIMESTAMP) var createdDate: Date? = null,
    @LastModifiedDate @Temporal(TemporalType.TIMESTAMP) var modifiedDate: Date? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false
)

@Entity
class Post(
    @Column(nullable = false) val userId: Long,
    @Column(nullable = false) var content: String,
    @ColumnDefault(value = "0") var view: Long = 0,
//    @Column(nullable = false) val attachmentId: Long,
) : BaseEntity()

@Entity
class Like(
    val userId :Long,
    @ManyToOne
    val post: Post,

) :BaseEntity()
