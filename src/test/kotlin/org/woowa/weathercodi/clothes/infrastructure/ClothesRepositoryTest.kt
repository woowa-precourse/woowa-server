package org.woowa.weathercodi.clothes.infrastructure

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.woowa.weathercodi.clothes.domain.Category
import org.woowa.weathercodi.clothes.domain.SubCategory

@DataJpaTest
class ClothesRepositoryTest(
    @Autowired val jpa: ClothesJpaRepository
) {

    @Test
    fun `사용자 소유 옷 전체를 조회한다`() {
        jpa.save(ClothesJpaEntity(null, 1L, "p1", Category.TOP, SubCategory.SHORT_SLEEVE_TEE))
        jpa.save(ClothesJpaEntity(null, 1L, "p2", Category.BOTTOM, SubCategory.JEANS))
        jpa.save(ClothesJpaEntity(null, 1L, "p3", Category.OUTER, SubCategory.COAT))
        jpa.save(ClothesJpaEntity(null, 2L, "p4", Category.TOP, SubCategory.SHORT_SLEEVE_TEE))

        val result = jpa.findByUserId(1L)

        assertThat(result).hasSize(3)
        assertThat(result.map { it.image }).containsExactlyInAnyOrder("p1", "p2", "p3")
    }

    @Test
    fun `사용자 소유 옷을 카테고리별로 조회한다`() {
        jpa.save(ClothesJpaEntity(null, 1L, "p1", Category.TOP, SubCategory.SHORT_SLEEVE_TEE))
        jpa.save(ClothesJpaEntity(null, 1L, "p2", Category.BOTTOM, SubCategory.JEANS))
        jpa.save(ClothesJpaEntity(null, 2L, "p3", Category.TOP, SubCategory.SHORT_SLEEVE_TEE))

        val result = jpa.findByUserIdAndCategory(1L, Category.TOP)

        assertThat(result).hasSize(1)
        assertThat(result[0].image).isEqualTo("p1")
    }

    @Test
    fun `옷 상세 조회가 정상적으로 동작한다`() {
        val saved = jpa.save(
            ClothesJpaEntity(null, 1L, "photo", Category.TOP, SubCategory.SHORT_SLEEVE_TEE)
        )

        val found = jpa.findById(saved.id!!).get()

        assertThat(found.image).isEqualTo("photo")
        assertThat(found.category).isEqualTo(Category.TOP)
    }

    @Test
    fun `옷 삭제가 정상적으로 동작한다`() {
        val saved = jpa.save(
            ClothesJpaEntity(null, 1L, "photo", Category.TOP, SubCategory.SHORT_SLEEVE_TEE)
        )

        jpa.delete(saved)

        val result = jpa.findById(saved.id!!)
        assertThat(result).isEmpty
    }
}