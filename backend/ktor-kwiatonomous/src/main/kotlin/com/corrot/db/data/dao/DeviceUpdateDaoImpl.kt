package com.corrot.db.data.dao

import com.corrot.db.DeviceUpdates
import com.corrot.db.data.model.DeviceUpdate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DeviceUpdateDaoImpl(private val database: Database) : DeviceUpdateDao {

    override fun init() =
        transaction(database) {
            SchemaUtils.create(DeviceUpdates)
        }

    override fun getAllDeviceUpdates(): List<DeviceUpdate> =
        transaction(database) {
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

    override fun getAllDeviceUpdates(deviceID: String): List<DeviceUpdate> =
        transaction(database) {
            DeviceUpdates.select { DeviceUpdates.deviceID eq deviceID }.map {
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

    override fun getLastDeviceUpdates(deviceID: String, count: Int): List<DeviceUpdate> =
        transaction(database) {
            DeviceUpdates
                .select { DeviceUpdates.deviceID eq deviceID }
                .limit(count)
                .orderBy(DeviceUpdates.timestamp, SortOrder.ASC)
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

    override fun getDeviceUpdate(updateID: Int): DeviceUpdate? =
        transaction(database) {
            DeviceUpdates.select { DeviceUpdates.updateID eq updateID }.map {
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
        val deviceUpdate = transaction(database) {
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

