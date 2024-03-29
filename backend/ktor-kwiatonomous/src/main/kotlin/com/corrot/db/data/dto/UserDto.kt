package com.corrot.db.data.dto

import com.corrot.db.data.model.UserDevice

data class UserDto(
    val userId: String,
    val userName: String,
    val devices: List<UserDevice>,
    val registrationTimestamp: Long,
    var lastActivityTimestamp: Long,
) {
    override fun toString(): String {
        return "> userId: $userId\n" +
                "> userName: $userName\n" +
                "> devices: $devices\n" +
                "> registrationTimestamp: $registrationTimestamp\n" +
                "> lastActivityTimestamp: $lastActivityTimestamp"
    }
}
