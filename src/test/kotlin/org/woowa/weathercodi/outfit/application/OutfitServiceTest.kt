package org.woowa.weathercodi.outfit.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.woowa.weathercodi.outfit.domain.*

class OutfitServiceTest {

    private val repo = FakeOutfitRepository()
    private val clothesRepo = FakeOutfitClothesRepository()
    private val service = OutfitService(repo, clothesRepo)

    @Test
    fun `고정 코디와 일반 코디가 구분되어 반환된다`() {
        // given
        repo.save(Outfit(id = 1L, category = OutfitCategory.SUMMER, fixed = true, thumbnail = "t1"))
        repo.save(Outfit(id = 2L, category = OutfitCategory.SUMMER, fixed = false, thumbnail = "t2"))

        // when
        val result = service.getOutfitList(1L)

        // then
        assertThat(result.fixedOutfits).hasSize(1)
        assertThat(result.outfits).hasSize(1)
    }

    @Test
    fun `코디 상세 조회 시 옷 리스트가 ID 기준 최신순으로 반환된다`() {
        // given
        repo.save(Outfit(id = 10L, category = OutfitCategory.SUMMER, fixed = false, thumbnail = "t"))

        clothesRepo.save(OutfitClothes(id = 1L, outfitId = 10L, clothesId = 100L, image = "i1",
            xCoord = 0.0, yCoord = 0.0, zIndex = 1, scale = 1.0))

        clothesRepo.save(OutfitClothes(id = 2L, outfitId = 10L, clothesId = 200L, image = "i2",
            xCoord = 0.0, yCoord = 0.0, zIndex = 2, scale = 1.0))

        // when
        val result = service.getOutfit(10L)

        // then
        assertThat(result.clothes[0].id).isEqualTo(2L)
        assertThat(result.clothes[1].id).isEqualTo(1L)
    }

    @Test
    fun `코디 생성 시 Outfit 과 OutfitClothes 가 함께 저장된다`() {
        // given
        val request = CreateOutfitRequest(
            category = OutfitCategory.SUMMER,
            clothes = listOf(
                ClothesRequest(1L, 1.0, 2.0, 1, 1.0)
            ),
            thumbnail = "thumb"
        )

        // when
        val result = service.createOutfit(1L, request)

        // then
        assertThat(result.id).isNotNull()
        assertThat(clothesRepo.findByOutfitId(result.id!!)).hasSize(1)
    }

    @Test
    fun `코디 삭제 시 연관된 outfit_clothes 도 함께 삭제된다`() {
        // given
        repo.save(Outfit(id = 100L, category = OutfitCategory.SUMMER, fixed = false, thumbnail = "t"))

        clothesRepo.save(OutfitClothes(id = 10L, outfitId = 100L, clothesId = 1L,
            image = "i", xCoord = 0.0, yCoord = 0.0, zIndex = 1, scale = 1.0))

        // when
        service.deleteOutfit(100L)

        // then
        assertThat(clothesRepo.findByOutfitId(100L)).isEmpty()
        assertThat(repo.findById(100L)).isNull()
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
        return store.values.toList()
    }

    override fun findById(id: Long): Outfit? {
        return store[id]
    }

    override fun delete(outfit: Outfit) {
        store.remove(outfit.id)
    }
}

class FakeOutfitClothesRepository : OutfitClothesRepository {

    private val store = mutableMapOf<Long, MutableList<OutfitClothes>>()
    private var idSequence = 1L

    override fun save(outfitClothes: OutfitClothes): OutfitClothes {
        val id = outfitClothes.id ?: idSequence++
        val saved = outfitClothes.copy(id = id)

        store.computeIfAbsent(saved.outfitId!!) { mutableListOf() }.add(saved)
        return saved
    }

    override fun findByOutfitId(outfitId: Long): List<OutfitClothes> {
        return store[outfitId]?.toList() ?: emptyList()
    }

    override fun deleteAllByOutfitId(outfitId: Long) {
        store.remove(outfitId)
    }
}