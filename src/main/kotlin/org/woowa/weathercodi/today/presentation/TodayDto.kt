package org.woowa.weathercodi.today.presentation

data class TodayResponse(
    val region: String,
    val current: CurrentWeatherData,
    val hourly: List<HourlyWeatherData>,
    val outfits: List<OutfitRecommendationResponse>
)

data class CurrentWeatherData(
    val temperature: Double,
    val weather: String,
    val timestamp: String
)

data class HourlyWeatherData(
    val temperature: Double,
    val weather: String,
    val timestamp: String
)

data class OutfitRecommendationResponse(
    val id: Long,
    val thumbnail: String
)
