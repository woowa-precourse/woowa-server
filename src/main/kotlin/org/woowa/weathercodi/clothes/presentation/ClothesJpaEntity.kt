package org.woowa.weathercodi.clothes.presentation

import jakarta.persistence.Column
import jakarta.persistence.*
import org.woowa.weathercodi.clothes.domain.Category
import org.woowa.weathercodi.clothes.domain.Clothes
import org.woowa.weathercodi.clothes.domain.SubCategory

@Entity
@Table(name = "clothes")
class ClothesJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val image: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: Category,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val subCategory: SubCategory,
) {
    fun toDomain(): Clothes =
        Clothes(
            id = id,
            userId = userId,
            image = image,
            category = category,
            subCategory = subCategory
        )
}