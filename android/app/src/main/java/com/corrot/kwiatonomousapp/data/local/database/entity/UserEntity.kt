package com.corrot.kwiatonomousapp.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.corrot.kwiatonomousapp.domain.model.User
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val devices: String,
    val registrationTimestamp: LocalDateTime,
    var lastActivityTimestamp: LocalDateTime,
    val isLoggedIn: Boolean
)

fun UserEntity.toUser() = User(
    userId = userId,
    devices = Gson().fromJson(devices, object : TypeToken<List<UserDevice>>() {}.type),
    registrationTimestamp = registrationTimestamp,
    lastActivityTimestamp = lastActivityTimestamp,
    isLoggedIn = isLoggedIn
)
