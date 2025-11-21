package org.woowa.weathercodi.user.infrastructure

import org.springframework.stereotype.Component
import org.woowa.weathercodi.user.domain.User
import org.woowa.weathercodi.user.domain.UserRepository

@Component
class UserRepositoryImpl(
    private val jpaRepository: UserJpaRepository
) : UserRepository {

    override fun findByDeviceUuid(deviceUuid: String): User? =
        jpaRepository.findByDeviceUuid(deviceUuid)?.toDomain()

    override fun save(user: User): User =
        jpaRepository.save(UserJpaEntity.fromDomain(user)).toDomain()
}