package org.woowa.weathercodi.user.application

import org.woowa.weathercodi.user.domain.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserDeviceServiceTest {

    private val repo = FakeUserRepository()
    private val service = UserDeviceService(repo)

    @Test
    fun `기존 deviceUuid 가 있으면 lastAccessedAt 만 갱신된다`() {
        repo.save(User(null, "abc", 1000L))

        val result = service.registerOrUpdateDevice("abc")

        assertThat(result.lastAccessedAt).isGreaterThan(1000L)
    }

    @Test
    fun `존재하지 않는 deviceUuid 이면 새 User 가 생성된다`() {
        val result = service.registerOrUpdateDevice("new-device")

        assertThat(result.deviceUuid).isEqualTo("new-device")
        assertThat(result.lastAccessedAt).isNotNull()
    }
}