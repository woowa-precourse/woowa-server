package org.woowa.weathercodi.outfit.infrastructure

import org.springframework.data.jpa.repository.JpaRepository

interface OutfitJpaRepository : JpaRepository<OutfitJpaEntity, Long> {
    fun findAllByUserId(userId: Long): List<OutfitJpaEntity>
}
