package org.woowa.weathercodi.user.infrastructure

import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserJpaEntity, Long> {
    fun findByDeviceUuid(deviceUuid: String): UserJpaEntity?
}