package org.woowa.weathercodi.clothes.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.woowa.weathercodi.clothes.domain.*

class ClothesServiceTest {

    private lateinit var repo: FakeClothesRepository
    private lateinit var service: ClothesService

    @BeforeEach
    fun setUp() {
        repo = FakeClothesRepository()
        service = ClothesService(repo)
    }

    @Test
    fun `필터 없이 전체 조회`() {
        repo.save(Clothes(null, 1L, "img1", Category.TOP, SubCategory.SHORT_SLEEVE_TEE))
        repo.save(Clothes(null, 1L, "img2", Category.BOTTOM, SubCategory.JEANS))
        repo.save(Clothes(null, 2L, "img3", Category.TOP, SubCategory.SHORT_SLEEVE_TEE)) // 다른 유저

        val result = service.getClothes(
            userId = 1L,
            category = null,
            subCategory = null,
            cursor = null,
            size = 20
        )

        assertThat(result).hasSize(2)
        assertThat(result.map { it.image }).containsExactlyInAnyOrder("img1", "img2")
    }

    @Test
    fun `category 필터링`() {

        repo.save(Clothes(null, 1L, "top1", Category.TOP, SubCategory.SHORT_SLEEVE_TEE))
        repo.save(Clothes(null, 1L, "bottom1", Category.BOTTOM, SubCategory.JEANS))

        val result = service.getClothes(
            userId = 1L,
            category = Category.TOP,
            subCategory = null,
            cursor = null,
            size = 10
        )

        assertThat(result).hasSize(1)
        assertThat(result[0].image).isEqualTo("top1")
    }

    @Test
    fun `category + subCategory 필터링`() {

        repo.save(Clothes(null, 1L, "top1", Category.TOP, SubCategory.SHORT_SLEEVE_TEE))
        repo.save(Clothes(null, 1L, "top2", Category.TOP, SubCategory.LONG_SLEEVE_TEE))

        val result = service.getClothes(
            userId = 1L,
            category = Category.TOP,
            subCategory = SubCategory.SHORT_SLEEVE_TEE,
            cursor = null,
            size = 10
        )

        assertThat(result).hasSize(1)
        assertThat(result[0].image).isEqualTo("top1")
    }

    @Test
    fun `cursor 기반 페이징`() {

        val c1 = repo.save(Clothes(null, 1L, "img1", Category.TOP, SubCategory.SHORT_SLEEVE_TEE))
        val c2 = repo.save(Clothes(null, 1L, "img2", Category.BOTTOM, SubCategory.JEANS))
        val c3 = repo.save(Clothes(null, 1L, "img3", Category.OUTER, SubCategory.COAT))

        val result = service.getClothes(
            userId = 1L,
            category = null,
            subCategory = null,
            cursor = c1.id,
            size = 10
        )

        assertThat(result).hasSize(2)
        assertThat(result.map { it.image }).containsExactlyInAnyOrder("img2", "img3")
    }

    @Test
    fun `size 제한`() {

        repo.save(Clothes(null, 1L, "img1", Category.TOP, SubCategory.SHORT_SLEEVE_TEE))
        repo.save(Clothes(null, 1L, "img2", Category.BOTTOM, SubCategory.JEANS))
        repo.save(Clothes(null, 1L, "img3", Category.OUTER, SubCategory.COAT))

        val result = service.getClothes(
            userId = 1L,
            category = null,
            subCategory = null,
            cursor = null,
            size = 2
        )

        assertThat(result).hasSize(2)
    }
}


// ---------- Fake Repository ----------

class FakeClothesRepository : ClothesRepository {
    private val store = mutableMapOf<Long, Clothes>()
    private var seq = 1L

    override fun findByUserId(userId: Long): List<Clothes> =
        store.values.filter { it.userId == userId }

    override fun findByUserIdAndCategory(userId: Long, category: Category): List<Clothes> =
        store.values.filter { it.userId == userId && it.category == category }

    override fun findById(id: Long): Clothes? =
        store[id]

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