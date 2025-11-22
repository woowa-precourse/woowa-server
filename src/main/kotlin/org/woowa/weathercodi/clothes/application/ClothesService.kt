package org.woowa.weathercodi.clothes.application

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.woowa.weathercodi.clothes.domain.Category
import org.woowa.weathercodi.clothes.domain.Clothes
import org.woowa.weathercodi.clothes.domain.ClothesRepository
import org.woowa.weathercodi.clothes.domain.SubCategory
import org.woowa.weathercodi.clothes.presentation.ClothesRegisterRequest
import org.woowa.weathercodi.global.exception.CustomException
import org.woowa.weathercodi.global.exception.ErrorCode
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
            ?: throw CustomException(ErrorCode.USER_NOT_FOUND)

        val existing = repo.findById(clothesId)
            ?: throw CustomException(ErrorCode.CLOTHES_NOT_FOUND)

        if (existing.userId != user.id)
            throw throw CustomException(ErrorCode.ACCESS_DENIED)

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
            ?: throw CustomException(ErrorCode.USER_NOT_FOUND)

        val clothes = repo.findById(clothesId)
            ?: throw CustomException(ErrorCode.CLOTHES_NOT_FOUND)

        if (clothes.userId != user.id) {
            throw CustomException(ErrorCode.ACCESS_DENIED)
        }

        return clothes
    }

    @Transactional
    fun deleteClothes(deviceUuid: String, clothesId: Long) {
        val user = userDeviceService.getByDeviceUuid(deviceUuid)
            ?: throw CustomException(ErrorCode.USER_NOT_FOUND)

        val clothes = repo.findById(clothesId)
            ?: throw CustomException(ErrorCode.CLOTHES_NOT_FOUND)

        if (clothes.userId != user.id)
            throw CustomException(ErrorCode.ACCESS_DENIED)

        imageStorageService.deleteFile(clothes.image)

        repo.delete(clothesId)
    }
}