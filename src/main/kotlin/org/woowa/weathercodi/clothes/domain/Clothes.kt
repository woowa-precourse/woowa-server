package org.woowa.weathercodi.clothes.domain

class Clothes (
    val id: Long? = null,
    val userId: Long,
    val image: String,
    val category: Category,
    val subCategory: SubCategory,
) {
    fun update(
        image: String,
        category: Category,
        subCategory: SubCategory
    ): Clothes {
        return Clothes(
            id = this.id,
            userId = this.userId,
            image = image,
            category = category,
            subCategory = subCategory
        )
    }
}