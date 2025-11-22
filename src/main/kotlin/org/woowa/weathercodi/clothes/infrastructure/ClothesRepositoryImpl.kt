package org.woowa.weathercodi.clothes.infrastructure

import org.springframework.stereotype.Component
import org.woowa.weathercodi.clothes.domain.Category
import org.woowa.weathercodi.clothes.domain.Clothes
import org.woowa.weathercodi.clothes.domain.ClothesRepository

@Component
class ClothesRepositoryImpl(
    private val jpa: ClothesJpaRepository
) : ClothesRepository {

    override fun findByUserId(userId: Long): List<Clothes> =
        jpa.findByUserId(userId)
            .map { it.toDomain() }

    override fun findByUserIdAndCategory(
        userId: Long,
        category: Category
    ): List<Clothes> =
        jpa.findByUserIdAndCategory(userId, category)
            .map { it.toDomain() }

    override fun findById(id: Long): Clothes? =
        jpa.findById(id)
            .map { it.toDomain() }
            .orElse(null)

    override fun save(clothes: Clothes): Clothes {
        val entity = ClothesJpaEntity(
            id = clothes.id,
            userId = clothes.userId,
            image = clothes.image,
            category = clothes.category,
            subCategory = clothes.subCategory
        )

        val saved = jpa.save(entity)
        return saved.toDomain()
    }

    override fun delete(id: Long) {
        jpa.deleteById(id)
    }
}