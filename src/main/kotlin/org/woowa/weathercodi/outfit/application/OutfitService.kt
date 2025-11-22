package org.woowa.weathercodi.outfit.application

import org.springframework.stereotype.Service
import org.woowa.weathercodi.global.s3.ImageStorageService
import org.woowa.weathercodi.outfit.domain.OutfitRepository
import org.woowa.weathercodi.user.application.UserDeviceService

@Service
class OutfitService(
    private val repo: OutfitRepository,
    private val userDeviceService: UserDeviceService,
    private val imageStorageService: ImageStorageService,
) {

    fun create(deviceUuid: String)
}