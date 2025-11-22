package org.woowa.weathercodi.global.exception

import org.springframework.http.HttpStatus

enum class ErrorCode (
    val httpStatus: HttpStatus,
    val message: String
) {
    // 401: Unauthorized | 400: Bad Request | 403 Forbidden | 404 Not Found | 409 Conflict | 410 Gone
    USER_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "존재하지 않는 유저입니다."
    ),
    ACCESS_DENIED(
        HttpStatus.FORBIDDEN,
        "권한이 없는 유저입니다."
    ),
    CLOTHES_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "존재하지 않는 옷 번호입니다."
    ),
    OUTFIT_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "존재하지 않는 코디입니다."
    ),
    CLOTHES_NOT_REGISTERED(
        HttpStatus.BAD_REQUEST,
        "등록되지 않은 옷입니다."
    ),
    INVALID_OUTFIT_CATEGORY(
        HttpStatus.BAD_REQUEST,
        "유효하지 않은 코디 카테고리입니다."
    ),
}