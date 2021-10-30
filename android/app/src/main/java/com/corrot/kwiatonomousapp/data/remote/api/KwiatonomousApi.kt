package com.corrot.kwiatonomousapp.data.remote.api

import com.corrot.kwiatonomousapp.data.remote.dto.DeviceDto
import retrofit2.http.GET
import retrofit2.http.Path

interface KwiatonomousApi {

    @GET("/kwiatonomous/all")
    suspend fun getDevices(): List<DeviceDto>

    @GET("/kwiatonomous/{id}")
    suspend fun getDeviceById(@Path("id") id: String): DeviceDto
}