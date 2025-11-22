package org.woowa.weathercodi.outfit.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OutfitTest {

    @Test
    fun `코디 생성 시 카테고리와 fixed 기본값이 정상적으로 설정된다`() {
        val outfit = Outfit(
            category = OutfitCategory.SUMMER,
            fixed = false,
            thumbnail = "http://image"
        )

        assertThat(outfit.category).isEqualTo(OutfitCategory.SUMMER)
        assertThat(outfit.fixed).isFalse()
        assertThat(outfit.thumbnail).isEqualTo("http://image")
    }

    @Test
    fun `코디에 OutfitClothes 를 추가할 수 있다`() {
        val outfit = Outfit(
            category = OutfitCategory.SUMMER,
            fixed = false,
            thumbnail = "http://image"
        )

        val outfitClothes = OutfitClothes(
            clothesId = 1L,
            image = "http://aaa",
            xCoord = 10.0,
            yCoord = 20.0,
            zIndex = 1,
            scale = 1.0
        )

        outfit.addClothes(outfitClothes)

        assertThat(outfit.clothes).hasSize(1)
        assertThat(outfit.clothes[0].clothesId).isEqualTo(1L)
    }
}