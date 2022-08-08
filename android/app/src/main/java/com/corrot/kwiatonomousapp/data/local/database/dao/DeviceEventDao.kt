package com.corrot.kwiatonomousapp.data.local.database.dao

import androidx.room.*
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceEventDao {

    @Query("SELECT * FROM device_event WHERE deviceId = :deviceId ORDER BY timestamp DESC")
    fun getAll(deviceId: String): Flow<List<DeviceEventEntity>>

    @Query("SELECT * FROM device_event WHERE deviceId = :deviceId ORDER BY timestamp DESC  LIMIT :limit")
    fun getAll(deviceId: String, limit: Int): Flow<List<DeviceEventEntity>>

    @Query("SELECT * FROM device_event WHERE deviceId = :deviceId AND timestamp >= :fromDate AND timestamp <= :toDate  ORDER BY timestamp DESC")
    fun getAllByDate(deviceId: String, fromDate: Long, toDate: Long): Flow<List<DeviceEventEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(deviceEvent: DeviceEventEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(deviceEvents: List<DeviceEventEntity>): List<Long>

    @Update
    suspend fun update(deviceEvent: DeviceEventEntity)

    @Update
    suspend fun update(deviceEvents: List<DeviceEventEntity>)

    @Query("DELETE FROM device_event WHERE deviceId = :deviceId AND timestamp = :timestamp")
    suspend fun remove(deviceId: String, timestamp: Long)

    @Query("DELETE FROM device_event WHERE deviceId = :deviceId")
    suspend fun removeAllWithDeviceId(deviceId: String)

    @Query("DELETE FROM device_event")
    suspend fun removeAll()

    @Transaction
    suspend fun insertOrUpdate(deviceEvent: DeviceEventEntity) {
        val id = insert(deviceEvent)
        if (id == -1L) insert(deviceEvent)
    }

    @Transaction
    suspend fun insertOrUpdate(deviceEvents: List<DeviceEventEntity>) {
        val insertResult = insert(deviceEvents)
        val updateList = mutableListOf<DeviceEventEntity>()

        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(deviceEvents[i])
        }

        if (updateList.isNotEmpty()) update(updateList)
    }
}