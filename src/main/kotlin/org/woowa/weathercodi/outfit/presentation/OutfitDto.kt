package org.woowa.weathercodi.outfit.presentation

import org.woowa.weathercodi.outfit.domain.OutfitCategory

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.web.multipart.MultipartFile
import io.swagger.v3.oas.annotations.media.Schema

data class ClothesRequest(

    @Schema(example = "1", description = "옷 ID")
    @get:JsonProperty("id")
    val id: Long,

    @Schema(example = "10.0")
    @get:JsonProperty("xCoord")
    val xCoord: Double,

    @Schema(example = "20.0")
    @get:JsonProperty("yCoord")
    val yCoord: Double,

    @Schema(example = "1")
    @get:JsonProperty("zIndex")
    val zIndex: Int,

    @Schema(example = "1.0")
    @get:JsonProperty("scale")
    val scale: Double
)

data class CreateOutfitRequest(
    @Schema(
        description = "옷 리스트(JSON 문자열)",
        example = """[{"id":1,"xCoord":10,"yCoord":20,"zIndex":1,"scale":1}]"""
    )
    val clothes: String,

    @Schema(
        description = "카테고리",
        example = "SPRING"
    )
    val category: String,

    @Schema(description = "코디 사진")
    val thumbnail: MultipartFile
)

data class UpdateOutfitRequest(
    val clothes: List<ClothesRequest>,
    val category: OutfitCategory
)

data class OutfitResponse(
    val id: Long,
    val thumbnail: String
)

data class OutfitListResponse(
    val fixedOutfits: List<OutfitResponse>,
    val outfits: List<OutfitResponse>
)

data class ClothesDetailResponse(
    val id: Long,
    val image: String
)

data class OutfitDetailResponse(
    val id: Long,
    val thumbnail: String,
    val category: String,
    val clothes: List<ClothesDetailResponse>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class OutfitClothesResponse(
    @get:JsonProperty("id") val id: Long,
    @get:JsonProperty("image") val image: String,
    @get:JsonProperty("xCoord") val xCoord: Double,
    @get:JsonProperty("yCoord") val yCoord: Double,
    @get:JsonProperty("zIndex") val zIndex: Int,
    @get:JsonProperty("scale") val scale: Double
)

data class CreateOutfitResponse(
    val id: Long,
    val clothes: List<OutfitClothesResponse>,
    val category: String,
    val thumbnail: String
)

data class UpdateOutfitResponse(
    val id: Long,
    val clothes: List<OutfitClothesResponse>,
    val category: String,
    val thumbnail: String
)

data class ToggleFixedResponse(
    val id: Long,
    val fixed: Boolean
)
