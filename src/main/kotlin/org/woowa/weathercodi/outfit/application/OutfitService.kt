package org.woowa.weathercodi.outfit.application

import org.springframework.stereotype.Service
import org.woowa.weathercodi.outfit.domain.*

@Service
class OutfitService(
    private val outfitRepository: OutfitRepository,
    private val outfitClothesRepository: OutfitClothesRepository
) {

    fun getOutfitList(userId: Long): OutfitListResponse {
        val outfits = outfitRepository.findAllByUserId(userId)

        val fixedOutfits = outfits
            .filter { it.fixed }
            .map { OutfitResponse(id = it.id!!, thumbnail = it.thumbnail) }

        val normalOutfits = outfits
            .filter { !it.fixed }
            .map { OutfitResponse(id = it.id!!, thumbnail = it.thumbnail) }

        return OutfitListResponse(
            fixedOutfits = fixedOutfits,
            outfits = normalOutfits
        )
    }

    fun getOutfit(outfitId: Long): OutfitDetailResponse {
        val outfit = outfitRepository.findById(outfitId)
            ?: throw IllegalArgumentException("Outfit not found: $outfitId")

        val clothes = outfitClothesRepository.findByOutfitId(outfitId)
            .map { ClothesDetailResponse(id = it.clothesId, image = it.image) }

        return OutfitDetailResponse(
            id = outfit.id!!,
            thumbnail = outfit.thumbnail,
            category = outfit.category.name.lowercase(),
            clothes = clothes
        )
    }

    fun createOutfit(userId: Long, request: CreateOutfitRequest): CreateOutfitResponse {
        val outfit = Outfit(
            userId = userId,
            category = request.category,
            fixed = false,
            thumbnail = request.thumbnail
        )

        val savedOutfit = outfitRepository.save(outfit)

        val savedClothes = request.clothes.map { clothesReq ->
            val outfitClothes = OutfitClothes(
                outfitId = savedOutfit.id!!,
                clothesId = clothesReq.id,
                image = "image-url-${clothesReq.id}",
                xCoord = clothesReq.xCoord,
                yCoord = clothesReq.yCoord,
                zIndex = clothesReq.zIndex,
                scale = clothesReq.scale
            )
            outfitClothesRepository.save(outfitClothes)
        }

        return CreateOutfitResponse(
            id = savedOutfit.id!!,
            clothes = savedClothes.map {
                OutfitClothesResponse(
                    id = it.clothesId,
                    image = it.image,
                    xCoord = it.xCoord,
                    yCoord = it.yCoord,
                    zIndex = it.zIndex,
                    scale = it.scale
                )
            },
            category = savedOutfit.category.name.lowercase(),
            thumbnail = savedOutfit.thumbnail
        )
    }

    fun deleteOutfit(outfitId: Long) {
        val outfit = outfitRepository.findById(outfitId)
            ?: throw IllegalArgumentException("Outfit not found: $outfitId")

        outfitClothesRepository.deleteAllByOutfitId(outfitId)
        outfitRepository.delete(outfit)
    }

    fun updateOutfit(outfitId: Long, request: UpdateOutfitRequest): UpdateOutfitResponse {
        val outfit = outfitRepository.findById(outfitId)
            ?: throw IllegalArgumentException("Outfit not found: $outfitId")

        val updatedOutfit = outfit.update(
            category = request.category,
            fixed = outfit.fixed,
            thumbnail = outfit.thumbnail
        )

        outfitRepository.save(updatedOutfit)

        outfitClothesRepository.deleteAllByOutfitId(outfitId)

        val newClothes = request.clothes.map { clothesReq ->
            val outfitClothes = OutfitClothes(
                outfitId = outfitId,
                clothesId = clothesReq.id,
                image = "image-url-${clothesReq.id}",
                xCoord = clothesReq.xCoord,
                yCoord = clothesReq.yCoord,
                zIndex = clothesReq.zIndex,
                scale = clothesReq.scale
            )
            outfitClothesRepository.save(outfitClothes)
        }

        return UpdateOutfitResponse(
            id = updatedOutfit.id!!,
            clothes = newClothes.map {
                OutfitClothesResponse(
                    id = it.clothesId,
                    image = it.image,
                    xCoord = it.xCoord,
                    yCoord = it.yCoord,
                    zIndex = it.zIndex,
                    scale = it.scale
                )
            },
            category = updatedOutfit.category.name.lowercase(),
            thumbnail = updatedOutfit.thumbnail
        )
    }
}
