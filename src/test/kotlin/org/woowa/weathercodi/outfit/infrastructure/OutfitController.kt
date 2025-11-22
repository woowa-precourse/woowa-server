package org.woowa.weathercodi.outfit.infrastructure

@WebMvcTest(controllers = [OutfitController::class])
class OutfitControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var outfitService: OutfitService

    @Test
    fun `코디 목록 조회 API`() {
        whenever(outfitService.getOutfitList(1L)).thenReturn(
            OutfitListResponse(
                fixedOutfits = listOf(OutfitsResponse(1L, "aaa")),
                outfits = listOf(OutfitsResponse(2L, "bbb"))
            )
        )

        mockMvc.perform(get("/outfits").header("Authorization", "Bearer token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.fixedOutfits").isArray)
            .andExpect(jsonPath("$.outfits").isArray)
    }

    @Test
    fun `코디 상세 조회 API`() {
        whenever(outfitService.getOutfit(1L)).thenReturn(
            OutfitDetailResponse(
                id = 1L,
                thumbnail = "img",
                category = "summer",
                clothes = listOf(
                    ClothesResponse(10L, "img1")
                )
            )
        )

        mockMvc.perform(get("/outfits/1").header("Authorization", "Bearer token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
    }

    @Test
    fun `코디 삭제 API`() {
        mockMvc.perform(delete("/outfits/1").header("Authorization", "Bearer token"))
            .andExpect(status().isOk)
    }
}