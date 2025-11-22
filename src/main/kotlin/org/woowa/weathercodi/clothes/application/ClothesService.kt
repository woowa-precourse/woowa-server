package org.woowa.weathercodi.clothes.application

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.woowa.weathercodi.clothes.domain.Category
import org.woowa.weathercodi.clothes.domain.Clothes
import org.woowa.weathercodi.clothes.domain.ClothesRepository
import org.woowa.weathercodi.clothes.domain.SubCategory
import org.woowa.weathercodi.clothes.presentation.ClothesRegisterRequest
import org.woowa.weathercodi.global.s3.ImageStorageService
import org.woowa.weathercodi.user.application.UserDeviceService

@Service
class ClothesService(
    private val repo: ClothesRepository,
    private val userDeviceService: UserDeviceService,
    private val imageStorageService: ImageStorageService,
) {

    fun create(deviceUuid: String, category: Category, subCategory: SubCategory, image: String): Clothes {
        val user = userDeviceService.registerOrUpdateDevice(deviceUuid)

        val newClothes = Clothes(
            id = null,
            userId = user.id!!,
            image = image,
            category = category,
            subCategory = subCategory,
        )

        return repo.save(newClothes)
    }

    fun updateClothes(deviceUuid: String, category: Category, subCategory: SubCategory, clothesId: Long): Clothes {
        val user = userDeviceService.getByDeviceUuid(deviceUuid)
            ?: throw IllegalArgumentException("User not found")

        val existing = repo.findById(clothesId)
            ?: throw IllegalArgumentException("Clothes not found")

        if (existing.userId != user.id)
            throw IllegalAccessException("Cannot modify clothes of another user")

        val updated = existing.update(
            category = category,
            subCategory = subCategory
        )

        return repo.save(updated)
    }

    fun getClothes(
        deviceUuid: String,
        category: Category?,
        subCategory: SubCategory?,
        cursor: Long?,
        size: Int
    ): List<Clothes> {

        val user = userDeviceService.registerOrUpdateDevice(deviceUuid)

        var list = repo.findByUserId(user.id!!)

        if (category != null) list = list.filter { it.category == category }
        if (subCategory != null) list = list.filter { it.subCategory == subCategory }
        if (cursor != null) list = list.filter { (it.id ?: 0) > cursor }

        return list.sortedBy { it.id }.take(size)
    }

    fun getClothesDetail(deviceUuid: String, clothesId: Long): Clothes {
        val user = userDeviceService.getByDeviceUuid(deviceUuid)
            ?: throw IllegalArgumentException("User not found")

        val clothes = repo.findById(clothesId)
            ?: throw IllegalArgumentException("Clothes not found")

        if (clothes.userId != user.id) {
            throw IllegalAccessException("Cannot view clothes of another user")
        }

        return clothes
    }

    @Transactional
    fun deleteClothes(deviceUuid: String, clothesId: Long) {
        val user = userDeviceService.getByDeviceUuid(deviceUuid)
            ?: throw IllegalArgumentException("User not found")

        val clothes = repo.findById(clothesId)
            ?: throw IllegalArgumentException("Clothes not found")

        if (clothes.userId != user.id)
            throw IllegalAccessException("Cannot delete clothes of another user")

        imageStorageService.deleteFile(clothes.image)

        repo.delete(clothesId)
    }
}