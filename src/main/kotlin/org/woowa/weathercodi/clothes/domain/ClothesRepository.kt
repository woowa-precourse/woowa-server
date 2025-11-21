package org.woowa.weathercodi.clothes.domain

interface ClothesRepository {
    fun findByUserId(userId: Long): List<Clothes>

    fun findByUserIdAndCategory(
        userId: Long,
        category: Category
    ): List<Clothes>

    fun findById(id: Long): Clothes?

    fun save(clothes: Clothes): Clothes

    fun delete(id: Long)
}