package com.corrot.kwiatonomousapp.data.local.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.corrot.kwiatonomousapp.data.local.database.dao.*
import com.corrot.kwiatonomousapp.data.local.database.entity.*

@Database(
    entities = [
        UserEntity::class,
        DeviceEntity::class,
        DeviceUpdateEntity::class,
        DeviceConfigurationEntity::class,
        UserDeviceEntity::class
    ],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]
)

@TypeConverters(Converters::class)
abstract class KwiatonomousDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun deviceUpdateDao(): DeviceUpdateDao
    abstract fun deviceConfigurationDao(): DeviceConfigurationDao
    abstract fun userDao(): UserDao
    abstract fun userDeviceDao(): UserDeviceDao
}