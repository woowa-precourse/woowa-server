package org.woowa.weathercodi.outfit.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.woowa.weathercodi.clothes.domain.ClothesRepository
import org.woowa.weathercodi.global.exception.CustomException
import org.woowa.weathercodi.global.exception.ErrorCode
import org.woowa.weathercodi.outfit.domain.*

@Service
class OutfitService(
    private val outfitRepository: OutfitRepository,
    private val outfitClothesRepository: OutfitClothesRepository,
    private val clothesRepository: ClothesRepository
) {

    fun getOutfitList(userId: Long): List<Outfit> {
        return outfitRepository.findAllByUserId(userId)
    }

    fun getOutfit(outfitId: Long): Pair<Outfit, List<OutfitClothes>> {
        val outfit = outfitRepository.findById(outfitId)
            ?: throw CustomException(ErrorCode.OUTFIT_NOT_FOUND)

        val clothes = outfitClothesRepository.findByOutfitId(outfitId)

        return Pair(outfit, clothes)
    }

    fun createOutfit(userId: Long, category: OutfitCategory, thumbnail: String, clothesData: List<ClothesData>): Pair<Outfit, List<OutfitClothes>> {
        // 옷 소유권 검증
        clothesData.forEach { data ->
            val clothes = clothesRepository.findById(data.clothesId)
                ?: throw CustomException(ErrorCode.CLOTHES_NOT_FOUND)

            if (clothes.userId != userId) {
                throw CustomException(ErrorCode.CLOTHES_NOT_REGISTERED)
            }
        }

        val outfit = Outfit(
            userId = userId,
            category = category,
            fixed = false,
            thumbnail = thumbnail
        )

        val savedOutfit = outfitRepository.save(outfit)

        val savedClothes = clothesData.map { data ->
            val outfitClothes = OutfitClothes(
                outfitId = savedOutfit.id!!,
                clothesId = data.clothesId,
                image = data.image,
                xCoord = data.xCoord,
                yCoord = data.yCoord,
                zIndex = data.zIndex,
                scale = data.scale
            )
            outfitClothesRepository.save(outfitClothes)
        }

        return Pair(savedOutfit, savedClothes)
    }

    @Transactional
    fun deleteOutfit(outfitId: Long) {
        val outfit = outfitRepository.findById(outfitId)
            ?: throw CustomException(ErrorCode.OUTFIT_NOT_FOUND)

        outfitClothesRepository.deleteAllByOutfitId(outfitId)
        outfitRepository.delete(outfit)
    }

    @Transactional
    fun updateOutfit(outfitId: Long, thumbnail: String, category: OutfitCategory, clothesData: List<ClothesData>): Pair<Outfit, List<OutfitClothes>> {
        val outfit = outfitRepository.findById(outfitId)
            ?: throw CustomException(ErrorCode.OUTFIT_NOT_FOUND)

        // 옷 소유권 검증
        clothesData.forEach { data ->
            val clothes = clothesRepository.findById(data.clothesId)
                ?: throw CustomException(ErrorCode.CLOTHES_NOT_FOUND)

            if (clothes.userId != outfit.userId) {
                throw CustomException(ErrorCode.CLOTHES_NOT_REGISTERED)
            }
        }

        val updatedOutfit = outfit.update(
            category = category,
            fixed = outfit.fixed,
            thumbnail = thumbnail
        )

        outfitRepository.save(updatedOutfit)
        outfitClothesRepository.deleteAllByOutfitId(outfitId)

        val newClothes = clothesData.map { data ->
            val outfitClothes = OutfitClothes(
                outfitId = outfitId,
                clothesId = data.clothesId,
                image = data.image,
                xCoord = data.xCoord,
                yCoord = data.yCoord,
                zIndex = data.zIndex,
                scale = data.scale
            )
            outfitClothesRepository.save(outfitClothes)
        }

        return Pair(updatedOutfit, newClothes)
    }

    fun toggleFixedOutfit(outfitId: Long): Outfit {
        val outfit = outfitRepository.findById(outfitId)
            ?: throw CustomException(ErrorCode.OUTFIT_NOT_FOUND)

        val updatedOutfit = outfit.update(
            category = outfit.category,
            fixed = !outfit.fixed,
            thumbnail = outfit.thumbnail
        )

        return outfitRepository.save(updatedOutfit)
    }
}

data class ClothesData(
    val clothesId: Long,
    val image: String,
    val xCoord: Double,
    val yCoord: Double,
    val zIndex: Int,
    val scale: Double
)
