package org.woowa.weathercodi.outfit.infrastructure

import jakarta.persistence.*
import org.woowa.weathercodi.outfit.domain.Outfit
import org.woowa.weathercodi.outfit.domain.OutfitCategory

@Entity
@Table(name = "outfit")
class OutfitJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: OutfitCategory,

    @Column(nullable = false)
    val fixed: Boolean = false,

    @Column(nullable = false)
    val thumbnail: String
) {
    fun toDomain(): Outfit =
        Outfit(
            id = id,
            userId = userId,
            category = category,
            fixed = fixed,
            thumbnail = thumbnail
        )

    companion object {
        fun fromDomain(outfit: Outfit): OutfitJpaEntity =
            OutfitJpaEntity(
                id = outfit.id,
                userId = outfit.userId,
                category = outfit.category,
                fixed = outfit.fixed,
                thumbnail = outfit.thumbnail
            )
    }
}
