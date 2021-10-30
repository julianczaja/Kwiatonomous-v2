package com.corrot.kwiatonomousapp.data.remote.api

import com.corrot.kwiatonomousapp.data.remote.dto.DeviceDto
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceUpdateDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface KwiatonomousApi {

    @GET("/kwiatonomous/all")
    suspend fun getDevices(): List<DeviceDto>

    @GET("/kwiatonomous/{id}")
    suspend fun getDeviceById(
        @Path("id") id: String
    ): DeviceDto

    @GET("/kwiatonomous/{id}/updates")
    suspend fun getDeviceUpdatesByDeviceId(
        @Path("id") id: String
    ): List<DeviceUpdateDto>

    @GET("/kwiatonomous/{id}/updates")
    suspend fun getDeviceUpdatesByDeviceId(
        @Path("id") id: String,
        @Query("limit") limit: Int
    ): List<DeviceUpdateDto>
}