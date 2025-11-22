package org.woowa.weathercodi.today.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.woowa.weathercodi.outfit.domain.OutfitCategory
import org.woowa.weathercodi.outfit.domain.OutfitRepository
import org.woowa.weathercodi.today.presentation.OutfitRecommendationResponse
import org.woowa.weathercodi.today.presentation.TodayResponse
import org.woowa.weathercodi.weather.application.WeatherApiClient

@Service
@Transactional(readOnly = true)
class TodayService(
    private val outfitRepository: OutfitRepository,
    private val weatherApiClient: WeatherApiClient
) {

    fun getTodayRecommendation(
        userId: Long,
        latitude: Double?,
        longitude: Double?,
        location: String?
    ): TodayResponse {
        // 날씨 정보 가져오기
        val weatherData = when {
            latitude != null && longitude != null -> {
                weatherApiClient.getCurrentWeather(latitude, longitude)
            }
            location != null -> {
                weatherApiClient.getCurrentWeatherByCity(location)
            }
            else -> {
                // 기본값: 서울
                weatherApiClient.getCurrentWeatherByCity("Seoul")
            }
        }

        val region = weatherData.name
        val temperature = weatherData.main.temp
        val weather = weatherData.weather.firstOrNull()?.main ?: "Clear"

        // 온도 기반 계절 판단
        val season = getSeasonFromTemperature(temperature)

        // 사용자의 코디 목록 조회
        val outfits = outfitRepository.findAllByUserId(userId)

        // 계절별 코디 필터링
        val recommendedOutfits = outfits
            .filter { it.category == season }
            .map { outfit ->
                OutfitRecommendationResponse(
                    id = outfit.id!!,
                    thumbnail = outfit.thumbnail
                )
            }

        return TodayResponse(
            region = region,
            temperature = temperature,
            weather = weather,
            outfits = recommendedOutfits
        )
    }

    private fun getSeasonFromTemperature(temperature: Double): OutfitCategory {
        return when {
            temperature >= 25 -> OutfitCategory.SUMMER  // 25°C 이상: 여름
            temperature >= 20 -> OutfitCategory.SPRING  // 20°C ~ 24°C: 봄
            temperature >= 10 -> OutfitCategory.AUTUMN  // 10°C ~ 19°C: 가을
            else -> OutfitCategory.WINTER               // 9°C 이하: 겨울
        }
    }
}
