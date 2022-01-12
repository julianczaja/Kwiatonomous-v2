package com.corrot.kwiatonomousapp.data.local.database.dao

import androidx.room.*
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {

    @Query("SELECT * FROM device")
    fun getAllDevices(): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM device WHERE deviceId = :deviceId")
    fun getDevice(deviceId: String): Flow<DeviceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDevice(device: DeviceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDevices(devices: List<DeviceEntity>)

    @Update
    suspend fun updateDevice(device: DeviceEntity)

    @Query("DELETE FROM device WHERE deviceId = :deviceId")
    suspend fun removeDevice(deviceId: String)

    @Query("DELETE FROM device")
    suspend fun removeAllDevices()
}