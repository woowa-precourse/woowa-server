package org.woowa.weathercodi.clothes.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.woowa.weathercodi.clothes.domain.Category

interface ClothesJpaRepository : JpaRepository<ClothesJpaEntity, Long> {

    fun findByUserId(userId: Long): List<ClothesJpaEntity>

    fun findByUserIdAndCategory(
        userId: Long,
        category: Category
    ): List<ClothesJpaEntity>
}