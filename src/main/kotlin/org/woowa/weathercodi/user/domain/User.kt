package org.woowa.weathercodi.user.domain

data class User(
    val id: Long? = null,
    val deviceUuid: String,
    val lastAccessedAt: Long // 마지막 시간만 간단하게 비교하면 되기에 Long 값이 편함
) {
    fun updateLastAccessed(now: Long): User =
        this.copy(lastAccessedAt = now)
}