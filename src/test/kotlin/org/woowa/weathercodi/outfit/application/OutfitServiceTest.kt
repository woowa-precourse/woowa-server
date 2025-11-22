package org.woowa.weathercodi.outfit.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.woowa.weathercodi.outfit.domain.*

class OutfitServiceTest {

    private lateinit var outfitRepo: FakeOutfitRepository
    private lateinit var outfitClothesRepo: FakeOutfitClothesRepository
    private lateinit var service: OutfitService

    @BeforeEach
    fun setUp() {
        outfitRepo = FakeOutfitRepository()
        outfitClothesRepo = FakeOutfitClothesRepository()
        service = OutfitService(outfitRepo, outfitClothesRepo)
    }

    @Test
    fun `코디 목록 조회 시 고정 코디와 일반 코디가 모두 반환된다`() {
        val userId = 1L
        outfitRepo.save(Outfit(id = 1L, userId = userId, category = OutfitCategory.SUMMER, fixed = true, thumbnail = "https://fixed1"))
        outfitRepo.save(Outfit(id = 2L, userId = userId, category = OutfitCategory.WINTER, fixed = true, thumbnail = "https://fixed2"))
        outfitRepo.save(Outfit(id = 3L, userId = userId, category = OutfitCategory.SUMMER, fixed = false, thumbnail = "https://normal1"))
        outfitRepo.save(Outfit(id = 4L, userId = userId, category = OutfitCategory.AUTUMN, fixed = false, thumbnail = "https://normal2"))

        val result = service.getOutfitList(userId)

        assertThat(result).hasSize(4)
        assertThat(result.filter { it.fixed }).hasSize(2)
        assertThat(result.filter { !it.fixed }).hasSize(2)
    }

    @Test
    fun `코디 상세 조회 시 Outfit과 OutfitClothes가 함께 반환된다`() {
        val outfitId = 1L
        outfitRepo.save(Outfit(id = outfitId, userId = 1L, category = OutfitCategory.SUMMER, fixed = false, thumbnail = "https://thumb"))

        outfitClothesRepo.save(OutfitClothes(id = 1L, outfitId = outfitId, clothesId = 10L, image = "https://clothes1", xCoord = 1.0, yCoord = 2.0, zIndex = 1, scale = 1.0))
        outfitClothesRepo.save(OutfitClothes(id = 2L, outfitId = outfitId, clothesId = 20L, image = "https://clothes2", xCoord = 3.0, yCoord = 4.0, zIndex = 2, scale = 1.5))

        val (outfit, clothes) = service.getOutfit(outfitId)

        assertThat(outfit.id).isEqualTo(outfitId)
        assertThat(outfit.category).isEqualTo(OutfitCategory.SUMMER)
        assertThat(clothes).hasSize(2)
        assertThat(clothes[0].id).isEqualTo(2L)
    }

    @Test
    fun `코디 등록 시 Outfit과 OutfitClothes가 함께 저장된다`() {
        val userId = 1L
        val clothesData = listOf(
            ClothesData(clothesId = 1L, image = "img1", xCoord = 1.0, yCoord = 2.0, zIndex = 1, scale = 1.0),
            ClothesData(clothesId = 2L, image = "img2", xCoord = 3.0, yCoord = 4.0, zIndex = 2, scale = 1.5)
        )

        val (outfit, savedClothes) = service.createOutfit(
            userId = userId,
            category = OutfitCategory.SUMMER,
            thumbnail = "https://thumbnail",
            clothesData = clothesData
        )

        assertThat(outfit.id).isNotNull()
        assertThat(savedClothes).hasSize(2)
        assertThat(outfitClothesRepo.findByOutfitId(outfit.id!!)).hasSize(2)
    }

    @Test
    fun `코디 삭제 시 연관된 OutfitClothes도 함께 삭제된다`() {
        val outfitId = 1L
        outfitRepo.save(Outfit(id = outfitId, userId = 1L, category = OutfitCategory.SUMMER, fixed = false, thumbnail = "https://thumb"))
        outfitClothesRepo.save(OutfitClothes(id = 1L, outfitId = outfitId, clothesId = 10L, image = "https://img", xCoord = 0.0, yCoord = 0.0, zIndex = 1, scale = 1.0))
        outfitClothesRepo.save(OutfitClothes(id = 2L, outfitId = outfitId, clothesId = 20L, image = "https://img2", xCoord = 0.0, yCoord = 0.0, zIndex = 2, scale = 1.0))

        service.deleteOutfit(outfitId)

        assertThat(outfitRepo.findById(outfitId)).isNull()
        assertThat(outfitClothesRepo.findByOutfitId(outfitId)).isEmpty()
    }

    @Test
    fun `코디 수정 시 기존 OutfitClothes는 삭제되고 새로운 데이터로 대체된다`() {
        val outfitId = 1L
        outfitRepo.save(Outfit(id = outfitId, userId = 1L, category = OutfitCategory.SUMMER, fixed = false, thumbnail = "https://old-thumb"))
        outfitClothesRepo.save(OutfitClothes(id = 1L, outfitId = outfitId, clothesId = 10L, image = "https://old", xCoord = 0.0, yCoord = 0.0, zIndex = 1, scale = 1.0))

        val clothesData = listOf(
            ClothesData(clothesId = 20L, image = "img20", xCoord = 5.0, yCoord = 6.0, zIndex = 1, scale = 2.0),
            ClothesData(clothesId = 30L, image = "img30", xCoord = 7.0, yCoord = 8.0, zIndex = 2, scale = 1.5)
        )

        val (outfit, newClothes) = service.updateOutfit(
            outfitId = outfitId,
            category = OutfitCategory.WINTER,
            clothesData = clothesData
        )

        assertThat(outfit.category).isEqualTo(OutfitCategory.WINTER)
        assertThat(newClothes).hasSize(2)
        assertThat(newClothes[0].clothesId).isEqualTo(20L)
    }
}

class FakeOutfitRepository : OutfitRepository {

    private val store = mutableMapOf<Long, Outfit>()
    private var idSequence = 1L

    override fun save(outfit: Outfit): Outfit {
        val id = outfit.id ?: idSequence++
        val saved = outfit.copy(id = id)
        store[id] = saved
        return saved
    }

    override fun findAllByUserId(userId: Long): List<Outfit> {
        return store.values.filter { it.userId == userId }
    }

    override fun findById(id: Long): Outfit? {
        return store[id]
    }

    override fun delete(outfit: Outfit) {
        store.remove(outfit.id)
    }
}

class FakeOutfitClothesRepository : OutfitClothesRepository {

    private val store = mutableMapOf<Long, OutfitClothes>()
    private var idSequence = 1L

    override fun save(outfitClothes: OutfitClothes): OutfitClothes {
        val id = outfitClothes.id ?: idSequence++
        val saved = outfitClothes.copy(id = id)
        store[id] = saved
        return saved
    }

    override fun findByOutfitId(outfitId: Long): List<OutfitClothes> {
        return store.values.filter { it.outfitId == outfitId }.sortedByDescending { it.id }
    }

    override fun deleteAllByOutfitId(outfitId: Long) {
        val keysToRemove = store.values.filter { it.outfitId == outfitId }.mapNotNull { it.id }
        keysToRemove.forEach { store.remove(it) }
    }
}
