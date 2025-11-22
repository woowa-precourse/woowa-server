package org.woowa.weathercodi.global.exception

import org.springframework.http.HttpStatus

class CustomException(
    val status: HttpStatus,
    val errorMessage: String
) : RuntimeException(errorMessage) {

    constructor(errorCode: ErrorCode) : this(
        status = errorCode.httpStatus,
        errorMessage = errorCode.message
    )
}