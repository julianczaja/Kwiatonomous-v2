package com.corrot.kwiatonomousapp.data.local.database.dao

import androidx.room.*
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceConfigurationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceConfigurationDao {

    @Query("SELECT * FROM device_configuration WHERE deviceId = :deviceId")
    fun getByDeviceId(deviceId: String): Flow<DeviceConfigurationEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(deviceConfiguration: DeviceConfigurationEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(deviceConfigurations: List<DeviceConfigurationEntity>): List<Long>

    @Update
    suspend fun update(deviceConfiguration: DeviceConfigurationEntity)

    @Update
    suspend fun update(deviceConfigurations: List<DeviceConfigurationEntity>)

    @Query("DELETE FROM device_configuration WHERE deviceId = :deviceId")
    suspend fun removeByDeviceId(deviceId: String)

    @Query("DELETE FROM device_configuration")
    suspend fun removeAll()

    @Transaction
    suspend fun insertOrUpdate(deviceConfiguration: DeviceConfigurationEntity) {
        val id = insert(deviceConfiguration)
        if (id == -1L) update(deviceConfiguration)
    }

    @Transaction
    suspend fun insertOrUpdate(deviceConfigurations: List<DeviceConfigurationEntity>) {
        val insertResult = insert(deviceConfigurations)
        val updateList = mutableListOf<DeviceConfigurationEntity>()

        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(deviceConfigurations[i])
        }

        if (updateList.isNotEmpty()) update(updateList)
    }
}