package org.woowa.weathercodi.outfit.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OutfitClothesTest {

    @Test
    fun `OutfitClothes 생성 시 필드가 정상적으로 설정된다`() {
        val outfitClothes = OutfitClothes(
            id = null,
            outfitId = 1L,
            clothesId = 10L,
            image = "http://aaa",
            xCoord = 1.1,
            yCoord = 2.2,
            zIndex = 3,
            scale = 1.5
        )

        assertThat(outfitClothes.outfitId).isEqualTo(1L)
        assertThat(outfitClothes.clothesId).isEqualTo(10L)
        assertThat(outfitClothes.image).isEqualTo("http://aaa")
        assertThat(outfitClothes.xCoord).isEqualTo(1.1)
        assertThat(outfitClothes.yCoord).isEqualTo(2.2)
        assertThat(outfitClothes.zIndex).isEqualTo(3)
        assertThat(outfitClothes.scale).isEqualTo(1.5)
    }

    @Test
    fun `OutfitClothes 수정 시 좌표와 순서만 변경할 수 있다`() {
        val outfitClothes = OutfitClothes(
            id = 1L,
            outfitId = 1L,
            clothesId = 10L,
            image = "http://aaa",
            xCoord = 1.1,
            yCoord = 2.2,
            zIndex = 1,
            scale = 1.0
        )

        val updated = outfitClothes.update(
            xCoord = 5.5,
            yCoord = 6.6,
            zIndex = 2,
            scale = 2.0
        )

        assertThat(updated.id).isEqualTo(1L)
        assertThat(updated.outfitId).isEqualTo(1L)
        assertThat(updated.clothesId).isEqualTo(10L)
        assertThat(updated.image).isEqualTo("http://aaa")
        assertThat(updated.xCoord).isEqualTo(5.5)
        assertThat(updated.yCoord).isEqualTo(6.6)
        assertThat(updated.zIndex).isEqualTo(2)
        assertThat(updated.scale).isEqualTo(2.0)
    }
}