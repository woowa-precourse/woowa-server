package org.woowa.weathercodi.today.presentation

import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.*
import org.woowa.weathercodi.today.application.TodayService
import org.woowa.weathercodi.user.application.UserDeviceService

@RestController
@RequestMapping("/today")
class TodayController(
    private val todayService: TodayService,
    private val userDeviceService: UserDeviceService
) {

    @GetMapping
    fun getTodayRecommendation(
        @RequestHeader("X-DEVICE-ID") deviceUuid: String,
        @RequestParam(required = false)
        @Parameter(description = "위도", example = "37.5")
        latitude: Double?,
        @RequestParam(required = false)
        @Parameter(description = "경도", example = "126.9")
        longitude: Double?,
        @RequestParam(required = false)
        @Parameter(description = "위치명", example = "Seoul")
        loc: String?
    ): TodayResponse {
        val user = userDeviceService.registerOrUpdateDevice(deviceUuid)

        return todayService.getTodayRecommendation(
            userId = user.id!!,
            latitude = latitude,
            longitude = longitude,
            location = loc
        )
    }
}
