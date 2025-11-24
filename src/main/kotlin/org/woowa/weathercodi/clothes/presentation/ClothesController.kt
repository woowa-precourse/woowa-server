package org.woowa.weathercodi.clothes.presentation

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.woowa.weathercodi.clothes.application.ClothesService
import org.woowa.weathercodi.clothes.domain.Category
import org.woowa.weathercodi.clothes.domain.SubCategory
import org.woowa.weathercodi.global.s3.ImageStorageService
import org.woowa.weathercodi.user.application.UserDeviceService

@RestController
@RequestMapping("/clothes")
class ClothesController(
    private val clothesService: ClothesService,
    private val imageStorageService: ImageStorageService,
    private val userDeviceService: UserDeviceService
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createClothes(
        @RequestHeader("X-DEVICE-ID") deviceUuid: String,
        @RequestPart("image") image: MultipartFile,
        @RequestParam category: Category,
        @RequestParam subCategory: SubCategory
    ): ClothesResponse {
        val imageUrl = imageStorageService.uploadClothesImage(deviceUuid, image)

        val saved = clothesService.create(
            deviceUuid = deviceUuid,
            category,
            subCategory,
            image = imageUrl
        )

        return ClothesResponse.from(saved)
    }

    @PutMapping("/{clothesId}")
    fun updateClothes(
        @RequestHeader("X-DEVICE-ID") deviceUuid: String,
        @PathVariable clothesId: Long,
        @RequestParam category: Category,
        @RequestParam subCategory: SubCategory
    ): ClothesResponse {

        val updated = clothesService.updateClothes(
            deviceUuid = deviceUuid,
            category,
            subCategory,
            clothesId = clothesId,
        )

        return ClothesResponse.from(updated)
    }

    @GetMapping
    fun getClothes(
        @RequestHeader("X-DEVICE-ID") deviceUuid: String,
        @RequestParam(required = false) category: Category?,
        @RequestParam(required = false) sub: SubCategory?,
        @RequestParam(required = false) cursor: Long?,
        @RequestParam(defaultValue = "20") size: Int,
    ): List<ClothesResponse> {

        val clothes = clothesService.getClothes(
            deviceUuid = deviceUuid,
            category = category,
            subCategory = sub,
            cursor = cursor,
            size = size
        )

        return clothes.map { ClothesResponse.from(it) }
    }

    @GetMapping("/{clothesId}")
    fun getClothesDetail(
        @RequestHeader("X-DEVICE-ID") deviceUuid: String,
        @PathVariable clothesId: Long
    ): ClothesResponse {

        val clothes = clothesService.getClothesDetail(
            deviceUuid = deviceUuid,
            clothesId = clothesId
        )

        return ClothesResponse.from(clothes)
    }

    @DeleteMapping("/{clothesId}")
    fun deleteClothes(
        @RequestHeader("X-DEVICE-ID") deviceUuid: String,
        @PathVariable clothesId: Long
    ) {
        val user = userDeviceService.getByDeviceUuid(deviceUuid)

        clothesService.deleteClothes(deviceUuid, clothesId)
    }
}