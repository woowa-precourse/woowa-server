package org.woowa.weathercodi.clothes.application

import org.woowa.weathercodi.clothes.domain.Clothes
import org.woowa.weathercodi.clothes.domain.ClothesRepository
import org.woowa.weathercodi.clothes.presentation.ClothesRegisterRequest
import org.woowa.weathercodi.user.application.UserDeviceService

class ClothesService(
    private val repo: ClothesRepository,
    private val userDeviceService: UserDeviceService,
) {

    fun create(deviceUuid: String, clothes: ClothesRegisterRequest, image: String): Clothes {

        val user = userDeviceService.getByDeviceUuid(deviceUuid)
            ?: throw IllegalArgumentException("User not found")

        val newClothes = Clothes(
            id = null,
            user.id!!,
            image,
            category = clothes.category,
            subCategory = clothes.subCategory,
        )

        return repo.save(newClothes)
    }

    fun updateClothes(deviceUuid: String, request: ClothesRegisterRequest, clothesId: Long): Clothes {
        val user = userDeviceService.getByDeviceUuid(deviceUuid)
            ?: throw IllegalArgumentException("User not found")

        val existing = repo.findById(clothesId)
            ?: throw IllegalArgumentException("Clothes not found")

        if (existing.userId != user.id) {
            throw IllegalAccessException("Cannot modify clothes of another user")
        }

        val updated = existing.update(
            category = request.category,
            subCategory = request.subCategory
        )

        return repo.save(updated)
    }

    fun getAll(userId: Long): List<Clothes> =
        repo.findByUserId(userId)
}