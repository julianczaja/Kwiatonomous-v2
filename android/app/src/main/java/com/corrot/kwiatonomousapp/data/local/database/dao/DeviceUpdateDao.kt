package com.corrot.kwiatonomousapp.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceUpdateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceUpdateDao {

    @Query("SELECT * FROM device_update WHERE deviceId = :deviceId ORDER BY updateTime DESC")
    fun getAllDeviceUpdates(deviceId: String): Flow<List<DeviceUpdateEntity>>

    @Query("SELECT * FROM device_update WHERE deviceId = :deviceId ORDER BY updateTime DESC  LIMIT :limit")
    fun getAllDeviceUpdates(deviceId: String, limit: Int): Flow<List<DeviceUpdateEntity>>

    @Query("SELECT * FROM device_update WHERE deviceId = :deviceId AND updateTime >= :fromDate AND updateTime <= :toDate  ORDER BY updateTime DESC")
    fun getDeviceUpdatesByDate(
        deviceId: String,
        fromDate: Long,
        toDate: Long
    ): Flow<List<DeviceUpdateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDeviceUpdate(deviceUpdate: DeviceUpdateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDeviceUpdates(deviceUpdates: List<DeviceUpdateEntity>)

    @Query("DELETE FROM device_update WHERE deviceId = :deviceId")
    suspend fun removeAllDeviceUpdates(deviceId: String)

    @Query("DELETE FROM device_update")
    suspend fun removeAllDevicesUpdates()
}