package org.woowa.weathercodi.user.application

import org.woowa.weathercodi.user.domain.User
import org.woowa.weathercodi.user.domain.UserRepository

class FakeUserRepository : UserRepository {

    private val data = mutableMapOf<String, User>()

    override fun findByDeviceUuid(deviceUuid: String): User? =
        data[deviceUuid]

    override fun save(user: User): User {
        data[user.deviceUuid] = user
        return user
    }
}