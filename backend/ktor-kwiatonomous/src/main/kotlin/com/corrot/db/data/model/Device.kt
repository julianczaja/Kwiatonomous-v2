package com.corrot.db.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Device(
    val deviceID: String,
    val birthday: Long,
    var lastUpdate: Long,
    var nextWatering: Long
)
