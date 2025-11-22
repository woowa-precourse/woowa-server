package org.woowa.weathercodi.global.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class ErrorDto(
    val httpStatus: HttpStatus,
    val message: String
) {
    companion object {
        fun toResponseEntity(ex: CustomException): ResponseEntity<ErrorDto> =
            ResponseEntity
                .status(ex.status)
                .body(
                    ErrorDto(
                        httpStatus = ex.status,
                        message = ex.errorMessage
                    )
                )
    }
}