package org.woowa.weathercodi.clothes.presentation

import org.woowa.weathercodi.clothes.domain.Category
import org.woowa.weathercodi.clothes.domain.SubCategory

data class ClothesRegisterRequest(
    val category: Category,
    val subCategory: SubCategory
)