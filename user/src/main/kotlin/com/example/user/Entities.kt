package com.example.user

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

@Table(indexes = [Index(name = "un_index", columnList = "username", unique = true)])
@Entity
class User(
    @Column(length = 32, nullable = false, unique = true) var username: String,
    @Column(length = 64, nullable = false, unique = true) var email: String,
    @Column(length = 64, nullable = false) var password: String,
    @Column(length = 12, nullable = false, unique = true) var phoneNumber: String,
    @Enumerated(EnumType.STRING) var gender: Gender,
    @Column(length = 64) var firstName: String? = null,
    @Column(length = 64) var lastName: String? = null,
    @Column(length = 64) var birthDate: Date? = null,
    @Column(columnDefinition = "TEXT") var bio: String? = null,
    @ColumnDefault(value = "false") val isActive: Boolean? = false,
//    @Column val photo: Attachment,
) : BaseEntity()

@Table(name = "follows")
@Entity
class Follow(
    @ManyToOne
    var following: User,

    @ManyToOne
    var follower: User,

    @Enumerated(EnumType.STRING) var status: FollowStatus,
) : BaseEntity()

@Entity
class Message(
    @ManyToOne
    var sender: User,

    @ManyToOne
    var receiver: User,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String

) : BaseEntity()


