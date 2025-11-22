package org.woowa.weathercodi.outfit.domain

data class OutfitClothes(
    val id: Long? = null,
    val outfitId: Long,
    val clothesId: Long,
    val image: String,
    val xCoord: Double,
    val yCoord: Double,
    val zIndex: Int,
    val scale: Double
) {
    fun update(
        xCoord: Double,
        yCoord: Double,
        zIndex: Int,
        scale: Double
    ): OutfitClothes {
        return OutfitClothes(
            id = this.id,
            outfitId = this.outfitId,
            clothesId = this.clothesId,
            image = this.image,
            xCoord = xCoord,
            yCoord = yCoord,
            zIndex = zIndex,
            scale = scale
        )
    }
}