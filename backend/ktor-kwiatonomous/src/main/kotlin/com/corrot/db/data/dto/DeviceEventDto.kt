package com.corrot.db.data.dto

data class DeviceEventDto(
    val timestamp: Long,
    val type: String,
    val data: String
) {

    override fun toString(): String {
        return "> timestamp: $timestamp\n" +
                "> type: $type\n" +
                "> data: $data"
    }
}
