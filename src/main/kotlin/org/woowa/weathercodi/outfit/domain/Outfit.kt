package org.woowa.weathercodi.outfit.domain

data class Outfit(
    val id: Long? = null,
    val userId: Long,
    val category: OutfitCategory,
    val fixed: Boolean = false,
    val thumbnail: String
) {
    fun update(
        category: OutfitCategory,
        fixed: Boolean,
        thumbnail: String
    ): Outfit {
        return Outfit(
            id = this.id,
            userId = this.userId,
            category = category,
            fixed = fixed,
            thumbnail = thumbnail
        )
    }
}