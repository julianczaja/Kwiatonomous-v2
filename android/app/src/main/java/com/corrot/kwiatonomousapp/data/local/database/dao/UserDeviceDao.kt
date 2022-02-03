package com.corrot.kwiatonomousapp.data.local.database.dao

import androidx.room.*
import com.corrot.kwiatonomousapp.data.local.database.entity.UserDeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDeviceDao {

    @Query("SELECT * FROM user_device")
    fun getAllUserDevices(): Flow<List<UserDeviceEntity>>

    @Query("SELECT * FROM user_device WHERE deviceId = :deviceId")
    fun getUserDevice(deviceId: String): Flow<UserDeviceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUserDevice(device: UserDeviceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUserDevices(devices: List<UserDeviceEntity>)

    @Update
    suspend fun updateUserDevice(device: UserDeviceEntity)

    @Query("DELETE FROM user_device WHERE deviceId = :deviceId")
    suspend fun removeUserDevice(deviceId: String)

    @Query("DELETE FROM user_device")
    suspend fun removeAllUserDevices()
}