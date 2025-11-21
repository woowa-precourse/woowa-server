package org.woowa.weathercodi.user.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.woowa.weathercodi.user.domain.User
import org.woowa.weathercodi.user.domain.UserRepository

@Service
class UserDeviceService(
    private val userRepository: UserRepository
) {

    @Transactional
    fun registerOrUpdateDevice(deviceUuid: String): User {
        val now: Long = System.currentTimeMillis()

        val existing: User? = userRepository.findByDeviceUuid(deviceUuid)
        if (existing != null) {
            val updated = existing.updateLastAccessed(now)
            return userRepository.save(updated)
        }

        val newUser: User = User(
            id = null,
            deviceUuid = deviceUuid,
            lastAccessedAt = now
        )

        return userRepository.save(newUser)
    }

    @Transactional(readOnly = true) // DB 읽기 전용
    fun getByDeviceUuid(deviceUuid: String): User? =
        userRepository.findByDeviceUuid(deviceUuid)
}