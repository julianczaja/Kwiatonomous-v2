package com.corrot.kwiatonomousapp.data.local.database

import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.corrot.kwiatonomousapp.data.local.database.dao.*
import com.corrot.kwiatonomousapp.data.local.database.entity.*


@Database(
    entities = [
        UserEntity::class,
        DeviceEntity::class,
        DeviceUpdateEntity::class,
        DeviceConfigurationEntity::class,
        DeviceEventEntity::class
    ],
    version = 8,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, spec = KwiatonomousDatabaseAutoMigrationFrom2To3::class),
        AutoMigration(from = 3, to = 4, spec = KwiatonomousDatabaseAutoMigrationFrom3To4::class),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8, spec = KwiatonomousDatabaseAutoMigrationFrom7To8::class),
    ]
)
@TypeConverters(Converters::class)
abstract class KwiatonomousDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun deviceUpdateDao(): DeviceUpdateDao
    abstract fun deviceConfigurationDao(): DeviceConfigurationDao
    abstract fun userDao(): UserDao
    abstract fun deviceEventDao(): DeviceEventDao
}

@DeleteTable.Entries(DeleteTable(tableName = "user_device"))
class KwiatonomousDatabaseAutoMigrationFrom2To3 : AutoMigrationSpec

@DeleteColumn.Entries(
    DeleteColumn(tableName = "device_update", columnName = "lastUpdated"),
    DeleteColumn(tableName = "device_update", columnName = "deviceUpdateId")
)
class KwiatonomousDatabaseAutoMigrationFrom3To4 : AutoMigrationSpec

@DeleteColumn.Entries(
    DeleteColumn(tableName = "device_configuration", columnName = "lastUpdated"),
    DeleteColumn(tableName = "device", columnName = "lastUpdated")
)
class KwiatonomousDatabaseAutoMigrationFrom7To8 : AutoMigrationSpec