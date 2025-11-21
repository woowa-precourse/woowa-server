package org.woowa.weathercodi.user.infrastructure

import jakarta.persistence.*
import org.woowa.weathercodi.user.domain.User

@Entity
@Table(name = "users")
class UserJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(unique = true, nullable = false)
    var deviceUuid: String,

    @Column(nullable = false)
    var lastAccessedAt: Long
) {
    fun toDomain(): User =
        User(
            id = id,
            deviceUuid = deviceUuid,
            lastAccessedAt = lastAccessedAt
        )

    companion object {
        fun fromDomain(user: User): UserJpaEntity =
            UserJpaEntity(
                id = user.id,
                deviceUuid = user.deviceUuid,
                lastAccessedAt = user.lastAccessedAt
            )
    }
}