package org.woowa.weathercodi.outfit.presentation

import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.woowa.weathercodi.outfit.application.OutfitService
import org.woowa.weathercodi.outfit.domain.Outfit
import org.woowa.weathercodi.outfit.domain.OutfitCategory
import org.woowa.weathercodi.outfit.domain.OutfitClothes
import org.woowa.weathercodi.user.application.UserDeviceService
import org.woowa.weathercodi.user.domain.User

@WebMvcTest(controllers = [OutfitController::class])
class OutfitControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var outfitService: OutfitService

    @MockitoBean
    lateinit var userDeviceService: UserDeviceService

    @Test
    fun `코디 목록 조회 API`() {
        val deviceUuid = "test-device-uuid"
        val userId = 1L

        `when`(userDeviceService.registerOrUpdateDevice(anyString())).thenReturn(
            User(id = userId, deviceUuid = deviceUuid, lastAccessedAt = System.currentTimeMillis())
        )

        `when`(outfitService.getOutfitList(anyLong())).thenReturn(
            listOf(
                Outfit(id = 1L, userId = userId, category = OutfitCategory.SUMMER, fixed = true, thumbnail = "https://fixed1"),
                Outfit(id = 2L, userId = userId, category = OutfitCategory.WINTER, fixed = true, thumbnail = "https://fixed2"),
                Outfit(id = 3L, userId = userId, category = OutfitCategory.SUMMER, fixed = false, thumbnail = "https://normal1"),
                Outfit(id = 4L, userId = userId, category = OutfitCategory.AUTUMN, fixed = false, thumbnail = "https://normal2")
            )
        )

        mockMvc.perform(
            get("/outfits")
                .header("X-DEVICE-ID", deviceUuid)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.fixedOutfits").isArray)
            .andExpect(jsonPath("$.fixedOutfits.length()").value(2))
            .andExpect(jsonPath("$.outfits").isArray)
            .andExpect(jsonPath("$.outfits.length()").value(2))
    }

    @Test
    fun `코디 상세 조회 API`() {
        val deviceUuid = "test-device-uuid"
        val outfitId = 1L

        `when`(userDeviceService.registerOrUpdateDevice(anyString())).thenReturn(
            User(id = 1L, deviceUuid = deviceUuid, lastAccessedAt = System.currentTimeMillis())
        )

        val outfit = Outfit(id = outfitId, userId = 1L, category = OutfitCategory.SUMMER, fixed = false, thumbnail = "https://thumbnail")
        val clothes = listOf(
            OutfitClothes(id = 1L, outfitId = outfitId, clothesId = 10L, image = "https://clothes1", xCoord = 1.0, yCoord = 2.0, zIndex = 1, scale = 1.0),
            OutfitClothes(id = 2L, outfitId = outfitId, clothesId = 20L, image = "https://clothes2", xCoord = 3.0, yCoord = 4.0, zIndex = 2, scale = 1.5)
        )

        `when`(outfitService.getOutfit(anyLong())).thenReturn(Pair(outfit, clothes))

        mockMvc.perform(
            get("/outfits/{id}", outfitId)
                .header("X-DEVICE-ID", deviceUuid)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(outfitId))
            .andExpect(jsonPath("$.thumbnail").value("https://thumbnail"))
            .andExpect(jsonPath("$.category").value("summer"))
            .andExpect(jsonPath("$.clothes").isArray)
            .andExpect(jsonPath("$.clothes.length()").value(2))
    }

    @Test
    fun `코디 등록 API`() {
        val deviceUuid = "test-device-uuid"
        val userId = 1L
        val clothesJson = """
            [
                {"id": 1, "xCoord": 1.0, "yCoord": 2.0, "zIndex": 1, "scale": 1.0},
                {"id": 2, "xCoord": 3.0, "yCoord": 4.0, "zIndex": 2, "scale": 1.5}
            ]
        """.trimIndent()

        `when`(userDeviceService.registerOrUpdateDevice(anyString())).thenReturn(
            User(id = userId, deviceUuid = deviceUuid, lastAccessedAt = System.currentTimeMillis())
        )

        val outfit = Outfit(id = 1L, userId = userId, category = OutfitCategory.SUMMER, fixed = false, thumbnail = "https://saved-thumbnail-url/test.jpg")
        val savedClothes = listOf(
            OutfitClothes(id = null, outfitId = 1L, clothesId = 1L, image = "image-url-1", xCoord = 1.0, yCoord = 2.0, zIndex = 1, scale = 1.0),
            OutfitClothes(id = null, outfitId = 1L, clothesId = 2L, image = "image-url-2", xCoord = 3.0, yCoord = 4.0, zIndex = 2, scale = 1.5)
        )

        `when`(outfitService.createOutfit(anyLong(), any(), anyString(), any())).thenReturn(
            Pair(outfit, savedClothes)
        )

        mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/outfits")
                .file(org.springframework.mock.web.MockMultipartFile("clothes", "", "application/json", clothesJson.toByteArray()))
                .file(org.springframework.mock.web.MockMultipartFile("category", "", "text/plain", "SUMMER".toByteArray()))
                .file(org.springframework.mock.web.MockMultipartFile("thumbnail", "test.jpg", "image/jpeg", "test-image-content".toByteArray()))
                .header("X-DEVICE-ID", deviceUuid)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.clothes.length()").value(2))
            .andExpect(jsonPath("$.category").value("summer"))
    }

    @Test
    fun `코디 삭제 API`() {
        val deviceUuid = "test-device-uuid"
        val outfitId = 1L

        `when`(userDeviceService.registerOrUpdateDevice(anyString())).thenReturn(
            User(id = 1L, deviceUuid = deviceUuid, lastAccessedAt = System.currentTimeMillis())
        )

        mockMvc.perform(
            delete("/outfits/{id}", outfitId)
                .header("X-DEVICE-ID", deviceUuid)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `코디 수정 API`() {
        val deviceUuid = "test-device-uuid"
        val outfitId = 1L
        val requestBody = """
            {
                "clothes": [
                    {"id": 20, "xCoord": 5.0, "yCoord": 6.0, "zIndex": 1, "scale": 2.0},
                    {"id": 30, "xCoord": 7.0, "yCoord": 8.0, "zIndex": 2, "scale": 1.5}
                ],
                "category": "WINTER"
            }
        """.trimIndent()

        `when`(userDeviceService.registerOrUpdateDevice(anyString())).thenReturn(
            User(id = 1L, deviceUuid = deviceUuid, lastAccessedAt = System.currentTimeMillis())
        )

        val outfit = Outfit(id = outfitId, userId = 1L, category = OutfitCategory.WINTER, fixed = false, thumbnail = "https://old-thumb")
        val newClothes = listOf(
            OutfitClothes(id = null, outfitId = outfitId, clothesId = 20L, image = "image-url-20", xCoord = 5.0, yCoord = 6.0, zIndex = 1, scale = 2.0),
            OutfitClothes(id = null, outfitId = outfitId, clothesId = 30L, image = "image-url-30", xCoord = 7.0, yCoord = 8.0, zIndex = 2, scale = 1.5)
        )

        `when`(outfitService.updateOutfit(anyLong(), any(), any())).thenReturn(
            Pair(outfit, newClothes)
        )

        mockMvc.perform(
            put("/outfits/{id}", outfitId)
                .header("X-DEVICE-ID", deviceUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(outfitId))
            .andExpect(jsonPath("$.category").value("winter"))
            .andExpect(jsonPath("$.clothes.length()").value(2))
    }
}
