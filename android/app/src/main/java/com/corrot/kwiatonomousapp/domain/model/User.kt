package com.corrot.kwiatonomousapp.domain.model

import com.corrot.kwiatonomousapp.data.local.database.entity.UserEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime

data class User(
    val userId: String,
    val userName: String,
    val devices: List<UserDevice>,
    val registrationTimestamp: LocalDateTime,
    var lastActivityTimestamp: LocalDateTime,
    var isLoggedIn: Boolean
)

fun User.toUserEntity() = UserEntity(
    userId = userId,
    userName = userName,
    devices = Gson().toJson(devices, object : TypeToken<List<UserDevice>>() {}.type),
    registrationTimestamp = registrationTimestamp,
    lastActivityTimestamp = lastActivityTimestamp,
    isLoggedIn = isLoggedIn
)