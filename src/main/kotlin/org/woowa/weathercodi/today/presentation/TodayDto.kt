package org.woowa.weathercodi.today.presentation

data class TodayResponse(
    val region: String,
    val temperature: Double,
    val weather: String,
    val outfits: List<OutfitRecommendationResponse>
)

data class OutfitRecommendationResponse(
    val id: Long,
    val thumbnail: String
)
