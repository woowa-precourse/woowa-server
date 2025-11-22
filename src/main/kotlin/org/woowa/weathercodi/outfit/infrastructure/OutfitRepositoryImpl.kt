package org.woowa.weathercodi.outfit.infrastructure

import org.springframework.stereotype.Repository
import org.woowa.weathercodi.outfit.domain.Outfit
import org.woowa.weathercodi.outfit.domain.OutfitRepository

@Repository
class OutfitRepositoryImpl(
    private val jpaRepository: OutfitJpaRepository
) : OutfitRepository {

    override fun save(outfit: Outfit): Outfit {
        val entity = OutfitJpaEntity.fromDomain(outfit)
        val saved = jpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun findAllByUserId(userId: Long): List<Outfit> {
        return jpaRepository.findAllByUserId(userId)
            .map { it.toDomain() }
    }

    override fun findById(id: Long): Outfit? {
        return jpaRepository.findById(id)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun delete(outfit: Outfit) {
        outfit.id?.let { jpaRepository.deleteById(it) }
    }
}
