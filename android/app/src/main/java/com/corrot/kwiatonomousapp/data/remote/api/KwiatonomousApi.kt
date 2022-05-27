package com.corrot.kwiatonomousapp.data.remote.api

import com.corrot.kwiatonomousapp.data.remote.dto.DeviceConfigurationDto
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceDto
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceUpdateDto
import com.corrot.kwiatonomousapp.data.remote.dto.UserDto
import com.corrot.kwiatonomousapp.domain.model.RegisterCredentials
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import retrofit2.Response
import retrofit2.http.*


interface KwiatonomousApi {

    // User
    @GET("/kwiatonomous/android/user")
    suspend fun getCurrentUser(): UserDto

    @POST("/kwiatonomous/android/register")
    suspend fun registerNewAccount(
        @Body registerCredentials: RegisterCredentials
    ): Response<String>

    @POST("/kwiatonomous/android/user/devices")
    suspend fun updateCurrentUserDevices(
        @Body devices: List<UserDevice>
    )

    // Device
    @GET("/kwiatonomous/android/device/all")
    suspend fun getDevices(): List<DeviceDto>

    @GET("/kwiatonomous/android/device/{deviceId}")
    suspend fun getDeviceById(
        @Path("deviceId") id: String
    ): DeviceDto

    @GET("/kwiatonomous/android/device/{deviceId}/nextwatering")
    suspend fun getNextWateringByDeviceId(
        @Path("deviceId") id: String
    ): Long

    @POST("/kwiatonomous/android/device/{deviceId}/nextwatering")
    suspend fun updateNextWateringByDeviceId(
        @Path("deviceId") id: String,
        @Body nextWatering: Long
    )

    // DeviceUpdate
    @GET("/kwiatonomous/android/device/{deviceId}/updates")
    suspend fun getAllDeviceUpdates(
        @Path("deviceId") id: String
    ): List<DeviceUpdateDto>

    @GET("/kwiatonomous/android/device/{deviceId}/updates")
    suspend fun getAllDeviceUpdates(
        @Path("deviceId") id: String,
        @Query("limit") limit: Int
    ): List<DeviceUpdateDto>

    @GET("/kwiatonomous/android/device/{deviceId}/updates")
    suspend fun getDeviceUpdatesByDate(
        @Path("deviceId") id: String,
        @Query("from") from: Long,
        @Query("to") to: Long
    ): List<DeviceUpdateDto>

    // DeviceConfiguration
    @GET("/kwiatonomous/android/device/{deviceId}/configuration")
    suspend fun getDeviceConfigurationByDeviceId(
        @Path("deviceId") id: String
    ): DeviceConfigurationDto

    @POST("/kwiatonomous/android/device/{deviceId}/configuration")
    suspend fun updateDeviceConfiguration(
        @Path("deviceId") id: String,
        @Body configuration: DeviceConfigurationDto
    )
}