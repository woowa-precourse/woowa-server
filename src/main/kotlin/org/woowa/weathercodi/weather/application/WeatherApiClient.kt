package org.woowa.weathercodi.weather.application

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class WeatherApiClient(
    @Value("\${weather.api.key}") private val apiKey: String,
    @Value("\${weather.api.base-url}") private val baseUrl: String,
    private val restTemplate: RestTemplate
) {

    fun getCurrentWeather(lat: Double, lon: Double): WeatherResponse {
        val url = "$baseUrl/weather?lat=$lat&lon=$lon&appid=$apiKey&units=metric&lang=kr"
        return restTemplate.getForObject(url, WeatherResponse::class.java)
            ?: throw RuntimeException("Failed to fetch weather data")
    }

    fun getCurrentWeatherByCity(cityName: String): WeatherResponse {
        val url = "$baseUrl/weather?q=$cityName&appid=$apiKey&units=metric&lang=kr"
        return restTemplate.getForObject(url, WeatherResponse::class.java)
            ?: throw RuntimeException("Failed to fetch weather data")
    }
}

data class WeatherResponse(
    val name: String,
    val coord: Coord,
    val main: MainWeather,
    val weather: List<Weather>
)

data class Coord(
    val lon: Double,
    val lat: Double
)

data class MainWeather(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)
