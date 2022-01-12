package com.corrot.kwiatonomousapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.corrot.kwiatonomousapp.data.local.database.dao.DeviceConfigurationDao
import com.corrot.kwiatonomousapp.data.local.database.dao.DeviceDao
import com.corrot.kwiatonomousapp.data.local.database.dao.DeviceUpdateDao
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceConfigurationEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceEntity
import com.corrot.kwiatonomousapp.data.local.database.entity.DeviceUpdateEntity

@Database(
    entities = [DeviceEntity::class, DeviceUpdateEntity::class, DeviceConfigurationEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class KwiatonomousDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun deviceUpdateDao(): DeviceUpdateDao
    abstract fun deviceConfigurationDao(): DeviceConfigurationDao
}