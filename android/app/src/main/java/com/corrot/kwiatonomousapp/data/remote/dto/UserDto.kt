package com.corrot.kwiatonomousapp.data.remote.dto

import com.corrot.kwiatonomousapp.common.toLocalDateTime
import com.corrot.kwiatonomousapp.data.local.database.entity.UserEntity
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class UserDto(
    val userId: String,
    val userName: String,
    val devices: List<UserDevice>,
    val registrationTimestamp: Long,
    var lastActivityTimestamp: Long,
)

fun UserDto.toUserEntity(isLoggedIn: Boolean) = UserEntity(
    userId = userId,
    userName = userName,
    devices = Gson().toJson(devices, object : TypeToken<List<UserDevice>>() {}.type),
    registrationTimestamp = registrationTimestamp.toLocalDateTime(),
    lastActivityTimestamp = lastActivityTimestamp.toLocalDateTime(),
    isLoggedIn = isLoggedIn
)
