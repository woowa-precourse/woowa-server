package org.woowa.weathercodi.outfit.domain

interface OutfitClothesRepository {
    fun save(outfitClothes: OutfitClothes): OutfitClothes
    fun findByOutfitId(outfitId: Long): List<OutfitClothes>
    fun deleteAllByOutfitId(outfitId: Long)
}