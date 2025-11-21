package org.woowa.weathercodi.clothes.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ClothesTest {

    @Test
    fun `Clothes 생성 시 필드가 정상적으로 설정된다`() {
        val clothes = Clothes(
            id = null,
            userId = 1L,
            photo = "photo_url",
            category = Category.TOP,
            subCategory = SubCategory.SHORT_SLEEVE
        )

        assertThat(clothes.userId).isEqualTo(1L)
        assertThat(clothes.photo).isEqualTo("photo_url")
        assertThat(clothes.category).isEqualTo(Category.TOP)
        assertThat(clothes.subCategory).isEqualTo(SubCategory.SHORT_SLEEVE)
    }

    @Test
    fun `Clothes 수정 시 원하는 필드만 변경할 수 있다`() {
        val clothes = Clothes(
            id = 1L,
            userId = 1L,
            photo = "old_photo",
            category = Category.TOP,
            subCategory = SubCategory.SHORT_SLEEVE
        )

        val updated = clothes.update(
            photo = "new_photo",
            category = Category.OUTER,
            subCategory = SubCategory.COAT
        )

        assertThat(updated.photo).isEqualTo("new_photo")
        assertThat(updated.category).isEqualTo(Category.OUTER)
        assertThat(updated.subCategory).isEqualTo(SubCategory.COAT)
    }
}