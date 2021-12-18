package com.corrot.kwiatonomousapp.data.remote.api

import com.corrot.kwiatonomousapp.data.remote.dto.DeviceConfigurationDto
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceDto
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceUpdateDto
import retrofit2.http.*

interface KwiatonomousApi {

    // Device
    @GET("/kwiatonomous/all")
    suspend fun getDevices(): List<DeviceDto>

    @GET("/kwiatonomous/{id}")
    suspend fun getDeviceById(
        @Path("id") id: String
    ): DeviceDto

    @GET("/kwiatonomous/{id}/nextwatering")
    suspend fun getNextWateringByDeviceId(
        @Path("id") id: String
    ): Long

    @POST("/kwiatonomous/{id}/nextwatering")
    suspend fun updateNextWateringByDeviceId(
        @Path("id") id: String,
        @Body nextWatering: Long
    )

    // DeviceUpdate
    @GET("/kwiatonomous/{id}/updates")
    suspend fun getAllDeviceUpdates(
        @Path("id") id: String
    ): List<DeviceUpdateDto>

    @GET("/kwiatonomous/{id}/updates")
    suspend fun getAllDeviceUpdates(
        @Path("id") id: String,
        @Query("limit") limit: Int
    ): List<DeviceUpdateDto>

    @GET("/kwiatonomous/{id}/updates")
    suspend fun getDeviceUpdatesByDate(
        @Path("id") id: String,
        @Query("from") from: Long,
        @Query("to") to: Long
    ): List<DeviceUpdateDto>

    // DeviceConfiguration
    @GET("/kwiatonomous/{id}/configuration")
    suspend fun getDeviceConfigurationByDeviceId(
        @Path("id") id: String
    ): DeviceConfigurationDto

    @POST("/kwiatonomous/{id}/configuration")
    suspend fun updateDeviceConfiguration(
        @Path("id") id: String,
        @Body configuration: DeviceConfigurationDto
    )
}