package org.woowa.weathercodi.user.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    fun `User 생성 시 deviceUuid 와 lastAccessedAt 이 정상적으로 설정된다`() {
        val uuid = "test-device-123"
        val now = System.currentTimeMillis()

        val user = User(
            id = null,
            deviceUuid = uuid,
            lastAccessedAt = now
        )

        assertThat(user.deviceUuid).isEqualTo(uuid)
        assertThat(user.lastAccessedAt).isEqualTo(now)
    }

    @Test
    fun `lastAccessedAt 을 업데이트하면 값이 변경된다`() {
        val user = User(
            id = null,
            deviceUuid = "abc",
            lastAccessedAt = 1000L
        )

        val updated = user.updateLastAccessed(2000L)

        assertThat(updated.lastAccessedAt).isEqualTo(2000L)
    }
}