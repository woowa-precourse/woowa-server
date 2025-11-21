package org.woowa.weathercodi.clothes.domain

class Clothes (
    val id: Long? = null,
    val userId: Long,
    val photo: String,
    val category: Category,
    val subCategory: SubCategory,
) {
    fun update(
        photo: String,
        category: Category,
        subCategory: SubCategory
    ): Clothes {
        return Clothes(
            id = this.id,
            userId = this.userId,
            photo = photo,
            category = category,
            subCategory = subCategory
        )
    }
}