package com.corrot.kwiatonomousapp.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.corrot.kwiatonomousapp.domain.model.User
import java.time.LocalDateTime

@Entity(tableName = "user")
data class UserEntity(

    @PrimaryKey
    val userId: String,

    val ha1: ByteArray,
    val addedDevicesIds: String, // JSON list of deviceId
    val registrationTimestamp: LocalDateTime,
    var lastActivityTimestamp: LocalDateTime,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserEntity

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

fun UserEntity.toUser() = User(
    userId = userId,
    ha1 = ha1,
    addedDevicesIds = addedDevicesIds.split(','),
    registrationTimestamp = registrationTimestamp,
    lastActivityTimestamp = lastActivityTimestamp
)
