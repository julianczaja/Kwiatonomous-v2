package com.corrot.db.data.dao

import com.corrot.db.DeviceUpdates
import com.corrot.db.KwiatonomousDatabase
import com.corrot.db.data.model.DeviceUpdate
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class DeviceUpdateDaoImpl(private val database: KwiatonomousDatabase) : DeviceUpdateDao {

    init {
        transaction(database.db) {
            arrayOf<Table>(DeviceUpdates)
            SchemaUtils.create(DeviceUpdates)
        }
    }

    override fun getAllDeviceUpdates(): List<DeviceUpdate> = transaction(database.db) {
        DeviceUpdates.selectAll().map {
            DeviceUpdate(
                updateId = it[DeviceUpdates.updateId],
                deviceId = it[DeviceUpdates.deviceId],
                timestamp = it[DeviceUpdates.timestamp],
                batteryLevel = it[DeviceUpdates.batteryLevel],
                batteryVoltage = it[DeviceUpdates.batteryVoltage],
                temperature = it[DeviceUpdates.temperature],
                humidity = it[DeviceUpdates.humidity]
            )
        }
    }

    override fun getAllDeviceUpdates(
        deviceId: String,
        limit: Int?,
        fromTimestamp: Long?,
        toTimestamp: Long?
    ): List<DeviceUpdate> = transaction(database.db) {
        DeviceUpdates
            .selectAll().where { DeviceUpdates.deviceId eq deviceId }
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
                    updateId = it[DeviceUpdates.updateId],
                    deviceId = it[DeviceUpdates.deviceId],
                    timestamp = it[DeviceUpdates.timestamp],
                    batteryLevel = it[DeviceUpdates.batteryLevel],
                    batteryVoltage = it[DeviceUpdates.batteryVoltage],
                    temperature = it[DeviceUpdates.temperature],
                    humidity = it[DeviceUpdates.humidity]
                )
            }
    }


    override fun getDeviceUpdate(deviceId: String, updateId: Int): DeviceUpdate? = transaction(database.db) {
        DeviceUpdates
            .selectAll().where { DeviceUpdates.deviceId eq deviceId }
            .andWhere { DeviceUpdates.updateId eq updateId }
            .map {
                DeviceUpdate(
                    updateId = it[DeviceUpdates.updateId],
                    deviceId = it[DeviceUpdates.deviceId],
                    timestamp = it[DeviceUpdates.timestamp],
                    batteryLevel = it[DeviceUpdates.batteryLevel],
                    batteryVoltage = it[DeviceUpdates.batteryVoltage],
                    temperature = it[DeviceUpdates.temperature],
                    humidity = it[DeviceUpdates.humidity]
                )
            }.singleOrNull()
    }

    override fun createDeviceUpdate(
        deviceId: String,
        timestamp: Long,
        batteryLevel: Int,
        batteryVoltage: Float,
        temperature: Float,
        humidity: Float
    ): Unit = transaction(database.db) {
        DeviceUpdates.insert {
            it[DeviceUpdates.deviceId] = deviceId
            it[DeviceUpdates.timestamp] = timestamp
            it[DeviceUpdates.batteryLevel] = batteryLevel
            it[DeviceUpdates.batteryVoltage] = batteryVoltage
            it[DeviceUpdates.temperature] = temperature
            it[DeviceUpdates.humidity] = humidity
        }
    }

    override fun deleteDeviceUpdate(updateId: Int) {
        DeviceUpdates.deleteWhere { DeviceUpdates.updateId eq updateId }
    }
}
