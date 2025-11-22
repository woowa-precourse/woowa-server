package org.woowa.weathercodi.clothes.presentation

import org.woowa.weathercodi.clothes.domain.Category
import org.woowa.weathercodi.clothes.domain.Clothes
import org.woowa.weathercodi.clothes.domain.SubCategory

data class ClothesResponse (
    val id: Long,
    val image: String,
    val category: Category,
    val subCategory: SubCategory,
) {
    companion object {
        fun from(clothes: Clothes): ClothesResponse =
            ClothesResponse(
                id = clothes.id!!,
                image = clothes.image,
                category = clothes.category,
                subCategory = clothes.subCategory
            )
    }
}
