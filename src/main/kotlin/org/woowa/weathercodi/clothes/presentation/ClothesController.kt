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
    private val imageStorageService: ImageStorageService, // 이미지 업로드 서비스(이미 만들어져 있다고 가정)
    private val userDeviceService: UserDeviceService
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createClothes(
        @RequestHeader("X-DEVICE-ID") deviceUuid: String,
        @RequestPart("image", required = true) image: MultipartFile,
        @RequestPart("data", required = true) request: ClothesRegisterRequest
    ): ClothesResponse {
        val imageUrl = imageStorageService.uploadClothesImage(deviceUuid, image)

        val saved = clothesService.create(
            deviceUuid = deviceUuid,
            clothes = request,
            image = imageUrl
        )

        return ClothesResponse.from(saved)
    }

    @PutMapping("/{clothesId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateClothes(
        @RequestHeader("X-DEVICE-ID") deviceUuid: String,
        @PathVariable clothesId: Long,
        @RequestPart("data") request: ClothesRegisterRequest,
    ): ClothesResponse {

        val updated = clothesService.updateClothes(
            deviceUuid = deviceUuid,
            request = request,
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

    @DeleteMapping("/{clothesId}")
    fun deleteClothes(
        @RequestHeader("X-DEVICE-ID") deviceUuid: String,
        @PathVariable clothesId: Long
    ) {
        val user = userDeviceService.getByDeviceUuid(deviceUuid)

        clothesService.deleteClothes(deviceUuid, clothesId)
    }
}