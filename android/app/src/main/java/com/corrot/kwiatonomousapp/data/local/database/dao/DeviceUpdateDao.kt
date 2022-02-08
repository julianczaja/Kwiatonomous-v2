package com.corrot.kwiatonomousapp.data.local.database.dao

import androidx.room.*
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceUpdateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceUpdateDao {

    @Query("SELECT * FROM device_update WHERE deviceId = :deviceId ORDER BY updateTime DESC")
    fun getAll(deviceId: String): Flow<List<DeviceUpdateEntity>>

    @Query("SELECT * FROM device_update WHERE deviceId = :deviceId ORDER BY updateTime DESC  LIMIT :limit")
    fun getAll(deviceId: String, limit: Int): Flow<List<DeviceUpdateEntity>>

    @Query("SELECT * FROM device_update WHERE deviceId = :deviceId AND updateTime >= :fromDate AND updateTime <= :toDate  ORDER BY updateTime DESC")
    fun getAllByDate(deviceId: String, fromDate: Long, toDate: Long): Flow<List<DeviceUpdateEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(deviceUpdate: DeviceUpdateEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(deviceUpdates: List<DeviceUpdateEntity>): List<Long>

    @Update
    suspend fun update(deviceUpdate: DeviceUpdateEntity)

    @Update
    suspend fun update(deviceUpdates: List<DeviceUpdateEntity>)

    @Query("DELETE FROM device_update WHERE deviceId = :deviceId")
    suspend fun removeAllWithDeviceId(deviceId: String)

    @Query("DELETE FROM device_update")
    suspend fun removeAll()

    @Transaction
    suspend fun insertOrUpdate(deviceUpdate: DeviceUpdateEntity) {
        val id = insert(deviceUpdate)
        if (id == -1L) insert(deviceUpdate)
    }

    @Transaction
    suspend fun insertOrUpdate(deviceUpdates: List<DeviceUpdateEntity>) {
        val insertResult = insert(deviceUpdates)
        val updateList = mutableListOf<DeviceUpdateEntity>()

        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(deviceUpdates[i])
        }

        if (updateList.isNotEmpty()) update(updateList)
    }
}