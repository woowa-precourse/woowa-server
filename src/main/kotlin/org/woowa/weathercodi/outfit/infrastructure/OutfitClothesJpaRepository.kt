package org.woowa.weathercodi.outfit.infrastructure

import org.springframework.data.jpa.repository.JpaRepository

interface OutfitClothesJpaRepository : JpaRepository<OutfitClothesJpaEntity, Long> {
    fun findByOutfitIdOrderByIdDesc(outfitId: Long): List<OutfitClothesJpaEntity>
    fun deleteAllByOutfitId(outfitId: Long)
}
