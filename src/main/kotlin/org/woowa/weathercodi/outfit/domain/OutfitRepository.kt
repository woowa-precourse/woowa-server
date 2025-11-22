package org.woowa.weathercodi.outfit.domain

interface OutfitRepository {
    fun save(outfit: Outfit): Outfit
    fun findAllByUserId(userId: Long): List<Outfit>
    fun findById(id: Long): Outfit?
    fun delete(outfit: Outfit)
}