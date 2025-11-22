package org.woowa.weathercodi.outfit.application

import org.woowa.weathercodi.outfit.domain.OutfitCategory

data class ClothesRequest(
    val id: Long,
    val xCoord: Double,
    val yCoord: Double,
    val zIndex: Int,
    val scale: Double
)

data class CreateOutfitRequest(
    val clothes: List<ClothesRequest>,
    val category: OutfitCategory,
    val thumbnail: String
)

data class UpdateOutfitRequest(
    val clothes: List<ClothesRequest>,
    val category: OutfitCategory
)

data class OutfitResponse(
    val id: Long,
    val thumbnail: String
)

data class OutfitListResponse(
    val fixedOutfits: List<OutfitResponse>,
    val outfits: List<OutfitResponse>
)

data class ClothesDetailResponse(
    val id: Long,
    val image: String
)

data class OutfitDetailResponse(
    val id: Long,
    val thumbnail: String,
    val category: String,
    val clothes: List<ClothesDetailResponse>
)

data class OutfitClothesResponse(
    val id: Long,
    val image: String,
    val xCoord: Double,
    val yCoord: Double,
    val zIndex: Int,
    val scale: Double
)

data class CreateOutfitResponse(
    val id: Long,
    val clothes: List<OutfitClothesResponse>,
    val category: String,
    val thumbnail: String
)

data class UpdateOutfitResponse(
    val id: Long,
    val clothes: List<OutfitClothesResponse>,
    val category: String,
    val thumbnail: String
)
