package org.woowa.weathercodi.outfit.presentation

import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.woowa.weathercodi.outfit.application.OutfitService

@WebMvcTest(controllers = [OutfitController::class])
class OutfitControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var outfitService: OutfitService

    @Test
    fun `코디 목록 조회 API`() {
        val deviceUuid = "test-device-uuid"
        whenever(outfitService.getOutfitList(deviceUuid)).thenReturn(
            OutfitListResponse(
                fixedOutfits = listOf(
                    OutfitResponse(id = 1L, thumbnail = "https://fixed1"),
                    OutfitResponse(id = 2L, thumbnail = "https://fixed2")
                ),
                outfits = listOf(
                    OutfitResponse(id = 3L, thumbnail = "https://normal1"),
                    OutfitResponse(id = 4L, thumbnail = "https://normal2")
                )
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
        val outfitId = 1L
        whenever(outfitService.getOutfit(outfitId)).thenReturn(
            OutfitDetailResponse(
                id = outfitId,
                thumbnail = "https://thumbnail",
                category = "summer",
                clothes = listOf(
                    ClothesResponse(id = 10L, image = "https://clothes1"),
                    ClothesResponse(id = 20L, image = "https://clothes2")
                )
            )
        )

        mockMvc.perform(
            get("/outfits/{id}", outfitId)
                .header("X-DEVICE-ID", "test-device-uuid")
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
        val requestBody = """
            {
                "clothes": [
                    {"id": 1, "xCoord": 1.0, "yCoord": 2.0, "zIndex": 1, "scale": 1.0},
                    {"id": 2, "xCoord": 3.0, "yCoord": 4.0, "zIndex": 2, "scale": 1.5}
                ],
                "category": "SUMMER",
                "thumbnail": "https://thumbnail"
            }
        """.trimIndent()

        whenever(outfitService.createOutfit(deviceUuid, any())).thenReturn(
            CreateOutfitResponse(
                id = 1L,
                clothes = listOf(
                    OutfitClothesResponse(id = 1L, image = "https://img1", xCoord = 1.0, yCoord = 2.0, zIndex = 1, scale = 1.0),
                    OutfitClothesResponse(id = 2L, image = "https://img2", xCoord = 3.0, yCoord = 4.0, zIndex = 2, scale = 1.5)
                ),
                category = "summer",
                thumbnail = "https://thumbnail"
            )
        )

        mockMvc.perform(
            post("/outfits")
                .header("X-DEVICE-ID", deviceUuid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.clothes.length()").value(2))
            .andExpect(jsonPath("$.category").value("summer"))
    }

    @Test
    fun `코디 삭제 API`() {
        val outfitId = 1L

        mockMvc.perform(
            delete("/outfits/{id}", outfitId)
                .header("X-DEVICE-ID", "test-device-uuid")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `코디 수정 API`() {
        val outfitId = 1L
        val deviceUuid = "test-device-uuid"
        val requestBody = """
            {
                "clothes": [
                    {"id": 20, "xCoord": 5.0, "yCoord": 6.0, "zIndex": 1, "scale": 2.0},
                    {"id": 30, "xCoord": 7.0, "yCoord": 8.0, "zIndex": 2, "scale": 1.5}
                ],
                "category": "WINTER"
            }
        """.trimIndent()

        whenever(outfitService.updateOutfit(outfitId, any())).thenReturn(
            UpdateOutfitResponse(
                id = outfitId,
                clothes = listOf(
                    OutfitClothesResponse(id = 20L, image = "https://img20", xCoord = 5.0, yCoord = 6.0, zIndex = 1, scale = 2.0),
                    OutfitClothesResponse(id = 30L, image = "https://img30", xCoord = 7.0, yCoord = 8.0, zIndex = 2, scale = 1.5)
                ),
                category = "winter",
                thumbnail = "https://old-thumb"
            )
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
