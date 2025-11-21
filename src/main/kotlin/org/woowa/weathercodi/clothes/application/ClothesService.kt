package org.woowa.weathercodi.clothes.application

import org.woowa.weathercodi.clothes.domain.Clothes
import org.woowa.weathercodi.clothes.domain.ClothesRepository
import org.woowa.weathercodi.clothes.presentation.ClothesRegisterRequest
import org.woowa.weathercodi.user.application.UserDeviceService

class ClothesService(
    private val clothesRepository: ClothesRepository,
    private val userDeviceService: UserDeviceService,
) {

    fun registerClothes(deviceUuid: String, clothes: ClothesRegisterRequest, image: String): Clothes {

        val user = userDeviceService.getByDeviceUuid(deviceUuid)
            ?: throw IllegalArgumentException("User not found")

        val newClothes = Clothes(
            id = null,
            user.id!!,
            image,
            category = clothes.category,
            subCategory = clothes.subCategory,
        )

        return clothesRepository.save(newClothes)
    }

    fun updateClothes(deviceUuid: String, request: ClothesRegisterRequest, imageUrl: String, clothesId: Long): Clothes {
        val user = userDeviceService.getByDeviceUuid(deviceUuid)
            ?: throw IllegalArgumentException("User not found")

        val existing = clothesRepository.findById(clothesId)
            ?: throw IllegalArgumentException("Clothes not found")

        if (existing.userId != user.id) {
            throw IllegalAccessException("Cannot modify clothes of another user")
        }

        val updated = existing.update(
            image = imageUrl,
            category = request.category,
            subCategory = request.subCategory
        )

        return clothesRepository.save(updated)
    }
}