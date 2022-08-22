package com.corrot.db.data.model

import com.corrot.db.data.dto.UserDto

data class User(
    val userId: String,
    val userName: String,
    val ha1: ByteArray,
    val devices: List<UserDevice>,
    val registrationTimestamp: Long,
    var lastActivityTimestamp: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (userId != other.userId) return false
        if (userName != other.userName) return false
        if (!ha1.contentEquals(other.ha1)) return false
        if (devices != other.devices) return false
        if (registrationTimestamp != other.registrationTimestamp) return false
        if (lastActivityTimestamp != other.lastActivityTimestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + ha1.contentHashCode()
        result = 31 * result + devices.hashCode()
        result = 31 * result + registrationTimestamp.hashCode()
        result = 31 * result + lastActivityTimestamp.hashCode()
        return result
    }
}

fun User.toUserDto() = UserDto(
    userId = userId,
    userName = userName,
    devices = devices,
    registrationTimestamp = registrationTimestamp,
    lastActivityTimestamp = lastActivityTimestamp
)