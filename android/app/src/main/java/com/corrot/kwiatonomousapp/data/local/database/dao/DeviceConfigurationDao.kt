package com.corrot.kwiatonomousapp.data.local.database.dao

import androidx.room.*
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceConfigurationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceConfigurationDao {

    @Query("SELECT * FROM device_configuration WHERE deviceId = :deviceId")
    fun getDeviceConfiguration(deviceId: String): Flow<DeviceConfigurationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDeviceConfiguration(deviceConfiguration: DeviceConfigurationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDeviceConfigurations(deviceConfigurations: List<DeviceConfigurationEntity>)

    @Query("DELETE FROM device_configuration WHERE deviceId = :deviceId")
    suspend fun removeDeviceConfiguration(deviceId: String)

    @Query("DELETE FROM device_configuration")
    suspend fun removeAllDevicesConfigurations()
}