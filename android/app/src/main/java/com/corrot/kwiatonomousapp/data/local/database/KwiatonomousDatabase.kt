package com.corrot.kwiatonomousapp.data.local.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.corrot.kwiatonomousapp.data.local.database.dao.DeviceConfigurationDao
import com.corrot.kwiatonomousapp.data.local.database.dao.DeviceDao
import com.corrot.kwiatonomousapp.data.local.database.dao.DeviceUpdateDao
import com.corrot.kwiatonomousapp.data.local.database.dao.UserDeviceDao
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceConfigurationEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceUpdateEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.UserDeviceEntity

@Database(
    entities = [
        DeviceEntity::class,
        DeviceUpdateEntity::class,
        DeviceConfigurationEntity::class,
        UserDeviceEntity::class
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)

@TypeConverters(Converters::class)
abstract class KwiatonomousDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun deviceUpdateDao(): DeviceUpdateDao
    abstract fun deviceConfigurationDao(): DeviceConfigurationDao
    abstract fun userDeviceDao(): UserDeviceDao
}