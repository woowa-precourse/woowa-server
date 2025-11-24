package org.woowa.weathercodi.outfit.presentation

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.woowa.weathercodi.clothes.domain.ClothesRepository
import org.woowa.weathercodi.global.exception.CustomException
import org.woowa.weathercodi.global.exception.ErrorCode
import org.woowa.weathercodi.global.s3.ImageStorageService
import org.woowa.weathercodi.outfit.application.ClothesData
import org.woowa.weathercodi.outfit.application.OutfitService
import org.woowa.weathercodi.outfit.domain.OutfitCategory
import org.woowa.weathercodi.user.application.UserDeviceService
import io.swagger.v3.oas.annotations.media.Schema

@RestController
@RequestMapping("/outfits")
class OutfitController(
    private val outfitService: OutfitService,
    private val userDeviceService: UserDeviceService,
    private val imageStorageService: ImageStorageService,
    private val objectMapper: ObjectMapper,
    private val clothesRepository: ClothesRepository
) {

    @GetMapping
    fun getOutfitList(
        @RequestHeader("X-DEVICE-ID") deviceUuid: String
    ): OutfitListResponse {
        val user = userDeviceService.registerOrUpdateDevice(deviceUuid)
        val outfits = outfitService.getOutfitList(user.id!!)

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

    @GetMapping("/{id}")
    fun getOutfit(
        @RequestHeader("X-DEVICE-ID") deviceUuid: String,
        @PathVariable id: Long
    ): OutfitDetailResponse {
        userDeviceService.registerOrUpdateDevice(deviceUuid)
        val (outfit, clothes) = outfitService.getOutfit(id)

        return OutfitDetailResponse(
            id = outfit.id!!,
            thumbnail = outfit.thumbnail,
            category = outfit.category.name.lowercase(),
            clothes = clothes.map { OutfitClothesResponse(id = it.clothesId, image = it.image, xCoord = it.xCoord, yCoord = it.yCoord, zIndex = it.zIndex, scale = it.scale) }
        )
    }

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createOutfit(
        @RequestHeader("X-DEVICE-ID") deviceUuid: String,
        @RequestPart("clothes")
        @Parameter(
            description = "옷 리스트 (JSON 문자열 형식)",
            example = """[{"id":1,"xCoord":10,"yCoord":20,"zIndex":1,"scale":1}]"""
        )
        clothes: String,
        @RequestPart("category")
        @Parameter(
            description = "카테고리 (SPRING, SUMMER, AUTUMN, WINTER)",
            example = "SPRING"
        )
        category: String,
        @RequestPart("thumbnail") thumbnail: MultipartFile
    ): CreateOutfitResponse {
        val clothesList: List<ClothesRequest> = objectMapper.readValue(clothes)

        val categoryEnum = try {
            OutfitCategory.valueOf(category.uppercase())
        } catch (e: IllegalArgumentException) {
            throw CustomException(ErrorCode.INVALID_OUTFIT_CATEGORY)
        }

        val user = userDeviceService.registerOrUpdateDevice(deviceUuid)
        val thumbnailUrl = imageStorageService.uploadOutfitThumbnail(deviceUuid, thumbnail)

        val clothesData = clothesList.map {
            val clothesEntity = clothesRepository.findById(it.id)
                ?: throw CustomException(ErrorCode.CLOTHES_NOT_FOUND)

            ClothesData(
                clothesId = it.id,
                image = clothesEntity.image,
                xCoord = it.xCoord,
                yCoord = it.yCoord,
                zIndex = it.zIndex,
                scale = it.scale
            )
        }

        val (outfit, savedClothes) = outfitService.createOutfit(
            userId = user.id!!,
            category = categoryEnum,
            thumbnail = thumbnailUrl,
            clothesData = clothesData
        )

        return CreateOutfitResponse(
            id = outfit.id!!,
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
            category = outfit.category.name.lowercase(),
            thumbnail = outfit.thumbnail
        )
    }

    @DeleteMapping("/{id}")
    fun deleteOutfit(
        @RequestHeader("X-DEVICE-ID") deviceUuid: String,
        @PathVariable id: Long
    ) {
        userDeviceService.registerOrUpdateDevice(deviceUuid)
        outfitService.deleteOutfit(id)
    }

    @PutMapping("/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateOutfit(
        @RequestHeader("X-DEVICE-ID") deviceUuid: String,
        @PathVariable id: Long,
        @RequestPart("clothes")
        @Parameter(
            description = "옷 리스트 (JSON 문자열 형식)",
            example = """[{"id":1,"xCoord":10,"yCoord":20,"zIndex":1,"scale":1}]"""
        )
        clothes: String,
        @RequestPart("category")
        @Parameter(
            description = "카테고리 (SPRING, SUMMER, AUTUMN, WINTER)",
            example = "SPRING"
        )
        category: String,
        @RequestPart("thumbnail") thumbnail: MultipartFile
    ): UpdateOutfitResponse {
        userDeviceService.registerOrUpdateDevice(deviceUuid)

        val thumbnailUrl = imageStorageService.uploadOutfitThumbnail(deviceUuid, thumbnail)

        val clothesList: List<ClothesRequest> = objectMapper.readValue(clothes)

        val categoryEnum = try {
            OutfitCategory.valueOf(category.uppercase())
        } catch (e: IllegalArgumentException) {
            throw CustomException(ErrorCode.INVALID_OUTFIT_CATEGORY)
        }

        val clothesData = clothesList.map {
            val clothesEntity = clothesRepository.findById(it.id)
                ?: throw CustomException(ErrorCode.CLOTHES_NOT_FOUND)

            ClothesData(
                clothesId = it.id,
                image = clothesEntity.image,
                xCoord = it.xCoord,
                yCoord = it.yCoord,
                zIndex = it.zIndex,
                scale = it.scale
            )
        }

        val (outfit, updatedClothes) = outfitService.updateOutfit(
            outfitId = id,
            category = categoryEnum,
            thumbnail = thumbnailUrl,
            clothesData = clothesData
        )

        return UpdateOutfitResponse(
            id = outfit.id!!,
            clothes = updatedClothes.map {
                OutfitClothesResponse(
                    id = it.clothesId,
                    image = it.image,
                    xCoord = it.xCoord,
                    yCoord = it.yCoord,
                    zIndex = it.zIndex,
                    scale = it.scale
                )
            },
            category = outfit.category.name.lowercase(),
            thumbnail = outfit.thumbnail
        )
    }

    @PatchMapping("/{id}/fixed")
    fun toggleFixedOutfit(
        @RequestHeader("X-DEVICE-ID") deviceUuid: String,
        @PathVariable id: Long
    ): ToggleFixedResponse {
        userDeviceService.registerOrUpdateDevice(deviceUuid)
        val outfit = outfitService.toggleFixedOutfit(id)

        return ToggleFixedResponse(
            id = outfit.id!!,
            fixed = outfit.fixed
        )
    }
}
