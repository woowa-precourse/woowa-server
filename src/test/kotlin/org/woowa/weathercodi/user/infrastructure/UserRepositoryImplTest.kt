package org.woowa.weathercodi.user.infrastructure

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException

@DataJpaTest
class UserRepositoryImplTest(

    @Autowired val jpa: UserJpaRepository
) {

    @Test
    fun `deviceUuid 는 unique 제약을 가진다`() {
        val e1 = UserJpaEntity(
            deviceUuid = "dup",
            lastAccessedAt = System.currentTimeMillis()
        )
        jpa.saveAndFlush(e1)

        val e2 = UserJpaEntity(
            deviceUuid = "dup",
            lastAccessedAt = System.currentTimeMillis()
        )

        assertThatThrownBy {
            jpa.saveAndFlush(e2)
        }.isInstanceOf(DataIntegrityViolationException::class.java)
    }
}