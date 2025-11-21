package org.woowa.weathercodi.user.domain

interface UserRepository {
    fun findByDeviceUuid(deviceUuid: String): User?
    fun save(user: User): User
}