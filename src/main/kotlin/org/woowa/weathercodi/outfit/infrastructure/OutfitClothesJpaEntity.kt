package org.woowa.weathercodi.outfit.infrastructure

import jakarta.persistence.*
import org.woowa.weathercodi.outfit.domain.OutfitClothes

@Entity
@Table(name = "outfit_clothes")
class OutfitClothesJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    val outfitId: Long,

    @Column(nullable = false)
    val clothesId: Long,

    @Column(nullable = false)
    val image: String,

    @Column(nullable = false)
    val xCoord: Double,

    @Column(nullable = false)
    val yCoord: Double,

    @Column(nullable = false)
    val zIndex: Int,

    @Column(nullable = false)
    val scale: Double
) {
    fun toDomain(): OutfitClothes =
        OutfitClothes(
            id = id,
            outfitId = outfitId,
            clothesId = clothesId,
            image = image,
            xCoord = xCoord,
            yCoord = yCoord,
            zIndex = zIndex,
            scale = scale
        )

    companion object {
        fun fromDomain(outfitClothes: OutfitClothes): OutfitClothesJpaEntity =
            OutfitClothesJpaEntity(
                id = outfitClothes.id,
                outfitId = outfitClothes.outfitId,
                clothesId = outfitClothes.clothesId,
                image = outfitClothes.image,
                xCoord = outfitClothes.xCoord,
                yCoord = outfitClothes.yCoord,
                zIndex = outfitClothes.zIndex,
                scale = outfitClothes.scale
            )
    }
}
