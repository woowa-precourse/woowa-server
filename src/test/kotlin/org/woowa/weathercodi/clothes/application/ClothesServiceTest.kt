package org.woowa.weathercodi.clothes.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ClothesServiceTest {

    private val repo = FakeClothesRepository()
    private val service = ClothesService(repo)

    @Test
    fun `사용자 소유 옷 전체를 조회한다`() {
        repo.save(Clothes(null, 1L, "p1", Category.TOP, SubCategory.SHORT_SLEEVE))
        repo.save(Clothes(null, 1L, "p2", Category.BOTTOM, SubCategory.JEANS))
        repo.save(Clothes(null, 1L, "p3", Category.OUTER, SubCategory.COAT))
        repo.save(Clothes(null, 2L, "p4", Category.TOP, SubCategory.SHORT_SLEEVE))

        val result = service.getAll(1L)

        assertThat(result).hasSize(3)
        assertThat(result.map { it.image }).containsExactlyInAnyOrder("p1", "p2", "p3")
    }

    @Test
    fun `사용자 소유 옷을 카테고리별로 조회한다`() {
        repo.save(Clothes(null, 1L, "p1", Category.TOP, SubCategory.SHORT_SLEEVE))
        repo.save(Clothes(null, 1L, "p2", Category.BOTTOM, SubCategory.JEANS))
        repo.save(Clothes(null, 2L, "p3", Category.TOP, SubCategory.SHORT_SLEEVE))

        val result = service.getByCategory(1L, Category.TOP)

        assertThat(result).hasSize(1)
        assertThat(result[0].image).isEqualTo("p1")
    }

    @Test
    fun `옷 상세 조회`() {
        val saved = repo.save(Clothes(null, 1L, "photo", Category.TOP, SubCategory.SHORT_SLEEVE))

        val result = service.getDetail(saved.id!!)

        assertThat(result.image).isEqualTo("photo")
        assertThat(result.category).isEqualTo(Category.TOP)
    }

    @Test
    fun `옷 등록`() {
        val result = service.create(
            userId = 1L,
            image = "p",
            category = Category.TOP,
            subCategory = SubCategory.SHORT_SLEEVE
        )

        assertThat(result.id).isNotNull()
        assertThat(result.image).isEqualTo("p")
    }

    @Test
    fun `옷 삭제`() {
        val saved = repo.save(Clothes(null, 1L, "p", Category.TOP, SubCategory.SHORT_SLEEVE))
        service.delete(saved.id!!)

        assertThat(repo.findById(saved.id!!)).isNull()
    }

    @Test
    fun `옷 수정`() {
        val saved = repo.save(Clothes(null, 1L, "old", Category.TOP, SubCategory.SHORT_SLEEVE))

        val updated = service.update(
            id = saved.id!!,
            category = Category.OUTER,
            subCategory = SubCategory.COAT
        )

        assertThat(updated.subCategory).isEqualTo(SubCategory.COAT)
        assertThat(updated.category).isEqualTo(Category.OUTER)
    }
}

class FakeClothesRepository : ClothesRepository {
    private val store = mutableMapOf<Long, Clothes>()
    private var seq = 1L

    override fun findByUserIdAndCategory(userId: Long, category: Category): List<Clothes> =
        store.values.filter { it.userId == userId && it.category == category }

    override fun findById(id: Long): Clothes? =
        store[id]

    override fun findByUserId(userId: Long): List<Clothes> =
        store.values.filter { it.userId == userId }

    override fun save(clothes: Clothes): Clothes {
        val id = clothes.id ?: seq++
        val saved = clothes.copy(id = id)
        store[id] = saved
        return saved
    }

    override fun delete(id: Long) {
        store.remove(id)
    }
}