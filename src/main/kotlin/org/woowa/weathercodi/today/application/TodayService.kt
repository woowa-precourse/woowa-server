package org.woowa.weathercodi.today.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.woowa.weathercodi.outfit.domain.OutfitCategory
import org.woowa.weathercodi.outfit.domain.OutfitRepository
import org.woowa.weathercodi.today.presentation.CurrentWeatherData
import org.woowa.weathercodi.today.presentation.HourlyWeatherData
import org.woowa.weathercodi.today.presentation.OutfitRecommendationResponse
import org.woowa.weathercodi.today.presentation.TodayResponse
import org.woowa.weathercodi.weather.application.WeatherApiClient
import org.woowa.weathercodi.weather.application.wmoCodeToDescription
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
        // 좌표 확정
        val (lat, lon) = when {
            latitude != null && longitude != null -> {
                Pair(latitude, longitude)
            }
            location != null -> {
                val weatherData = weatherApiClient.getCurrentWeatherByCity(location)
                Pair(weatherData.coord.lat, weatherData.coord.lon)
            }
            else -> {
                // 기본값: 서울
                val weatherData = weatherApiClient.getCurrentWeatherByCity("Seoul")
                Pair(weatherData.coord.lat, weatherData.coord.lon)
            }
        }

        // Open-Meteo API로 현재 날씨 + 시간별 예보 가져오기
        val openMeteoData = weatherApiClient.getOpenMeteoWeather(lat, lon)

        // 도시 이름 가져오기 (OpenWeatherMap API 사용)
        val currentWeatherData = weatherApiClient.getCurrentWeather(lat, lon)
        val region = currentWeatherData.name

        // 현재 날씨 정보
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val seoulZone = ZoneId.of("Asia/Seoul")
        val now = LocalDateTime.now(seoulZone)

        // current 데이터의 timestamp (문자열 그대로 사용)
        val currentTimestamp = openMeteoData.current.time
        val currentTemp = openMeteoData.current.temperature_2m
        val currentWeather = wmoCodeToDescription(openMeteoData.current.weather_code)

        // 현재 시간 이후의 가장 가까운 시간대 인덱스 찾기
        val currentHourIndex = openMeteoData.hourly.time.indexOfFirst { timeStr ->
            val hourTime = LocalDateTime.parse(timeStr, formatter)
            !hourTime.isBefore(now)
        }.let { if (it == -1) 0 else it }

        // 현재 시간부터 5시간 예보 (현재 포함, 0~4시간 후)
        val hourlyForecasts = (currentHourIndex until minOf(currentHourIndex + 5, openMeteoData.hourly.time.size))
            .map { index ->
                val timeStr = openMeteoData.hourly.time[index]

                HourlyWeatherData(
                    temperature = openMeteoData.hourly.temperature_2m[index],
                    weather = wmoCodeToDescription(openMeteoData.hourly.weather_code[index]),
                    timestamp = timeStr
                )
            }

        // 온도 기반 계절 판단 (현재 온도 기준)
        val season = getSeasonFromTemperature(currentTemp)

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
            current = CurrentWeatherData(
                temperature = currentTemp,
                weather = currentWeather,
                timestamp = currentTimestamp
            ),
            hourly = hourlyForecasts,
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
