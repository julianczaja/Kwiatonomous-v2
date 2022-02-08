package com.corrot.kwiatonomousapp.data.local.database.dao

import androidx.room.*
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {

    @Query("SELECT * FROM device")
    fun getAll(): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM device WHERE deviceId = :deviceId")
    fun getByDeviceId(deviceId: String): Flow<DeviceEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(device: DeviceEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(devices: List<DeviceEntity>): List<Long>

    @Update
    suspend fun update(device: DeviceEntity)

    @Update
    suspend fun update(devices: List<DeviceEntity>)

    @Query("DELETE FROM device WHERE deviceId = :deviceId")
    suspend fun removeByDeviceId(deviceId: String)

    @Query("DELETE FROM device")
    suspend fun removeAll()

    @Transaction
    suspend fun insertOrUpdate(device: DeviceEntity) {
        val id = insert(device)
        if (id == -1L) update(device)
    }

    @Transaction
    suspend fun insertOrUpdate(devices: List<DeviceEntity>) {
        val insertResult = insert(devices)
        val updateList = mutableListOf<DeviceEntity>()

        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(devices[i])
        }

        if (updateList.isNotEmpty()) update(updateList)
    }
}