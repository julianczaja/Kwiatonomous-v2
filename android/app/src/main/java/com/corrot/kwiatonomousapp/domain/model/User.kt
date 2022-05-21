package com.corrot.kwiatonomousapp.domain.model

import java.time.LocalDateTime

data class User(
    val userId: String,
    val ha1: ByteArray,
    val addedDevicesIds: List<String>,
    val registrationTimestamp: LocalDateTime,
    var lastActivityTimestamp: LocalDateTime,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (userId != other.userId) return false
        if (!ha1.contentEquals(other.ha1)) return false
        if (addedDevicesIds != other.addedDevicesIds) return false
        if (registrationTimestamp != other.registrationTimestamp) return false
        if (lastActivityTimestamp != other.lastActivityTimestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + ha1.contentHashCode()
        result = 31 * result + addedDevicesIds.hashCode()
        result = 31 * result + registrationTimestamp.hashCode()
        result = 31 * result + lastActivityTimestamp.hashCode()
        return result
    }
}