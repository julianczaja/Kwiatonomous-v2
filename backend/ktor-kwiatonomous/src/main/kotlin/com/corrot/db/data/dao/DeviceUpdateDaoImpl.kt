package com.corrot.db.data.dao

import com.corrot.db.DeviceUpdates
import com.corrot.db.KwiatonomousDatabase
import com.corrot.db.data.model.DeviceUpdate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DeviceUpdateDaoImpl(private val database: KwiatonomousDatabase) : DeviceUpdateDao {

    init {
        transaction(database.db) {
            SchemaUtils.create(DeviceUpdates)
        }
    }

    override fun getAllDeviceUpdates(): List<DeviceUpdate> =
        transaction(database.db) {
            DeviceUpdates.selectAll().map {
                DeviceUpdate(
                    updateID = it[DeviceUpdates.updateID],
                    deviceID = it[DeviceUpdates.deviceID],
                    timestamp = it[DeviceUpdates.timestamp],
                    batteryLevel = it[DeviceUpdates.batteryLevel],
                    batteryVoltage = it[DeviceUpdates.batteryVoltage],
                    temperature = it[DeviceUpdates.temperature],
                    humidity = it[DeviceUpdates.humidity]
                )
            }
        }

    override fun getAllDeviceUpdates(
        deviceID: String,
        limit: Int?,
        fromTimestamp: Long?,
        toTimestamp: Long?
    ): List<DeviceUpdate> =
        transaction(database.db) {
            DeviceUpdates
                .select { DeviceUpdates.deviceID eq deviceID }
                .apply {
                    if (fromTimestamp != null && toTimestamp != null) {
                        andWhere { DeviceUpdates.timestamp greaterEq fromTimestamp }
                        andWhere { DeviceUpdates.timestamp lessEq toTimestamp }
                    }
                }
                .apply {
                    if (limit != null) {
                        limit(limit)
                    }
                }
                .orderBy(DeviceUpdates.timestamp, SortOrder.DESC)
                .map {
                    DeviceUpdate(
                        updateID = it[DeviceUpdates.updateID],
                        deviceID = it[DeviceUpdates.deviceID],
                        timestamp = it[DeviceUpdates.timestamp],
                        batteryLevel = it[DeviceUpdates.batteryLevel],
                        batteryVoltage = it[DeviceUpdates.batteryVoltage],
                        temperature = it[DeviceUpdates.temperature],
                        humidity = it[DeviceUpdates.humidity]
                    )
                }
        }


    override fun getDeviceUpdate(deviceID: String, updateID: Int): DeviceUpdate? =
        transaction(database.db) {
            DeviceUpdates
                .select { DeviceUpdates.deviceID eq deviceID }
                .andWhere { DeviceUpdates.updateID eq updateID }
                .map {
                    DeviceUpdate(
                        updateID = it[DeviceUpdates.updateID],
                        deviceID = it[DeviceUpdates.deviceID],
                        timestamp = it[DeviceUpdates.timestamp],
                        batteryLevel = it[DeviceUpdates.batteryLevel],
                        batteryVoltage = it[DeviceUpdates.batteryVoltage],
                        temperature = it[DeviceUpdates.temperature],
                        humidity = it[DeviceUpdates.humidity]
                    )
                }.singleOrNull()
        }

    override fun createDeviceUpdate(
        deviceID: String,
        timestamp: Long,
        batteryLevel: Int,
        batteryVoltage: Float,
        temperature: Float,
        humidity: Float
    ): Int {
        val deviceUpdate = transaction(database.db) {
            return@transaction DeviceUpdates.insert {
                it[DeviceUpdates.deviceID] = deviceID
                it[DeviceUpdates.timestamp] = timestamp
                it[DeviceUpdates.batteryLevel] = batteryLevel
                it[DeviceUpdates.batteryVoltage] = batteryVoltage
                it[DeviceUpdates.temperature] = temperature
                it[DeviceUpdates.humidity] = humidity
            }
        }
        return deviceUpdate[DeviceUpdates.updateID]
    }


    override fun deleteDeviceUpdate(updateID: Int) {
        DeviceUpdates.deleteWhere { DeviceUpdates.updateID eq updateID }
    }
}

