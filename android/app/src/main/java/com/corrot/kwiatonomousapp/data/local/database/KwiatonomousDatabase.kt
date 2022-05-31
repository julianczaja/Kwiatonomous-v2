package com.corrot.kwiatonomousapp.data.local.database

import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.corrot.kwiatonomousapp.data.local.database.dao.DeviceConfigurationDao
import com.corrot.kwiatonomousapp.data.local.database.dao.DeviceDao
import com.corrot.kwiatonomousapp.data.local.database.dao.DeviceUpdateDao
import com.corrot.kwiatonomousapp.data.local.database.dao.UserDao
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceConfigurationEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceUpdateEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.UserEntity


@Database(
    entities = [
        UserEntity::class,
        DeviceEntity::class,
        DeviceUpdateEntity::class,
        DeviceConfigurationEntity::class,
    ],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(
            from = 2,
            to = 3,
            spec = KwiatonomousDatabaseAutoMigrationFrom2To3::class
        )
    ]
)
@TypeConverters(Converters::class)
abstract class KwiatonomousDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun deviceUpdateDao(): DeviceUpdateDao
    abstract fun deviceConfigurationDao(): DeviceConfigurationDao
    abstract fun userDao(): UserDao
}

@DeleteTable.Entries(DeleteTable(tableName = "user_device"))
class KwiatonomousDatabaseAutoMigrationFrom2To3 : AutoMigrationSpec
