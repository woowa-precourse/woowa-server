package org.woowa.weathercodi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WeatherCodiApplication

fun main(args: Array<String>) {
	runApplication<WeatherCodiApplication>(*args)
}
