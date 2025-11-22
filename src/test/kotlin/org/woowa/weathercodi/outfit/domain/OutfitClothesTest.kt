package org.woowa.weathercodi.outfit.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OutfitClothesTest {

    @Test
    fun `OutfitClothes 생성 시 좌표 값과 순서 값이 저장된다`() {
        val oc = OutfitClothes(
            clothesId = 10L,
            image = "http://aaa",
            xCoord = 1.1,
            yCoord = 2.2,
            zIndex = 3,
            scale = 1.0
        )

        assertThat(oc.clothesId).isEqualTo(10L)
        assertThat(oc.xCoord).isEqualTo(1.1)
        assertThat(oc.yCoord).isEqualTo(2.2)
        assertThat(oc.zIndex).isEqualTo(3)
    }

    @Test
    fun `clothesId 없이 OutfitClothes 생성 불가`() {
        assertThrows<IllegalArgumentException> {
            OutfitClothes(
                clothesId = null,
                image = "http://aaa",
                xCoord = 0.0,
                yCoord = 0.0,
                zIndex = 1,
                scale = 1.0
            )
        }
    }
}