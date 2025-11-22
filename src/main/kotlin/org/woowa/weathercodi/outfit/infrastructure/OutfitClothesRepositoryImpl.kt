package org.woowa.weathercodi.outfit.infrastructure

import org.springframework.stereotype.Repository
import org.woowa.weathercodi.outfit.domain.OutfitClothes
import org.woowa.weathercodi.outfit.domain.OutfitClothesRepository

@Repository
class OutfitClothesRepositoryImpl(
    private val jpaRepository: OutfitClothesJpaRepository
) : OutfitClothesRepository {

    override fun save(outfitClothes: OutfitClothes): OutfitClothes {
        val entity = OutfitClothesJpaEntity.fromDomain(outfitClothes)
        val saved = jpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun findByOutfitId(outfitId: Long): List<OutfitClothes> {
        return jpaRepository.findByOutfitIdOrderByIdDesc(outfitId)
            .map { it.toDomain() }
    }

    override fun deleteAllByOutfitId(outfitId: Long) {
        jpaRepository.deleteAllByOutfitId(outfitId)
    }
}
