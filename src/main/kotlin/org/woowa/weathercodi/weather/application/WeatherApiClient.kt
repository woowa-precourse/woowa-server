package org.woowa.weathercodi.weather.application

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class WeatherApiClient(
    @Value("\${weather.api.key}") private val apiKey: String,
    @Value("\${weather.api.base-url}") private val baseUrl: String,
    @Value("\${weather.open-meteo.base-url}") private val openMeteoBaseUrl: String,
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

    fun getOpenMeteoWeather(lat: Double, lon: Double): OpenMeteoResponse {
        val url = "$openMeteoBaseUrl/forecast?latitude=$lat&longitude=$lon" +
                "&current=temperature_2m,weather_code,relative_humidity_2m,apparent_temperature" +
                "&hourly=temperature_2m,weather_code,relative_humidity_2m,apparent_temperature" +
                "&timezone=Asia/Seoul&forecast_days=2"
        return restTemplate.getForObject(url, OpenMeteoResponse::class.java)
            ?: throw RuntimeException("Failed to fetch Open-Meteo weather data")
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

// Open-Meteo API Response
data class OpenMeteoResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: OpenMeteoCurrent,
    val hourly: OpenMeteoHourly
)

data class OpenMeteoCurrent(
    val time: String,
    val temperature_2m: Double,
    val weather_code: Int,
    val relative_humidity_2m: Int,
    val apparent_temperature: Double
)

data class OpenMeteoHourly(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val weather_code: List<Int>,
    val relative_humidity_2m: List<Int>,
    val apparent_temperature: List<Double>
)

// WMO Weather Code to English Description
fun wmoCodeToDescription(code: Int): String {
    return when (code) {
        0 -> "Clear"
        1, 2, 3 -> "Clouds"
        45, 48 -> "Fog"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rain"
        71, 73, 75, 77 -> "Snow"
        80, 81, 82 -> "Rain"
        85, 86 -> "Snow"
        95, 96, 99 -> "Thunderstorm"
        else -> "Unknown"
    }
}
