package com.corrot.db.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Device(
    val deviceId: String,
    val birthday: Long,
    var lastUpdate: Long,
    var nextWatering: Long
)
