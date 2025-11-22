package org.woowa.weathercodi.outfit.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OutfitTest {

    @Test
    fun `코디 생성 시 필드가 정상적으로 설정된다`() {
        val outfit = Outfit(
            id = null,
            userId = 1L,
            category = OutfitCategory.SUMMER,
            fixed = false,
            thumbnail = "http://image"
        )

        assertThat(outfit.userId).isEqualTo(1L)
        assertThat(outfit.category).isEqualTo(OutfitCategory.SUMMER)
        assertThat(outfit.fixed).isFalse()
        assertThat(outfit.thumbnail).isEqualTo("http://image")
    }

    @Test
    fun `코디 수정 시 원하는 필드만 변경할 수 있다`() {
        val outfit = Outfit(
            id = 1L,
            userId = 1L,
            category = OutfitCategory.SUMMER,
            fixed = false,
            thumbnail = "http://old-image"
        )

        val updated = outfit.update(
            category = OutfitCategory.WINTER,
            fixed = true,
            thumbnail = "http://new-image"
        )

        assertThat(updated.id).isEqualTo(1L)
        assertThat(updated.userId).isEqualTo(1L)
        assertThat(updated.category).isEqualTo(OutfitCategory.WINTER)
        assertThat(updated.fixed).isTrue()
        assertThat(updated.thumbnail).isEqualTo("http://new-image")
    }
}