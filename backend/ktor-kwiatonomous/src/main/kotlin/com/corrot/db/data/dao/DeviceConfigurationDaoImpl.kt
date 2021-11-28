package com.corrot.db.data.dao

import com.corrot.db.Devices
import com.corrot.db.DevicesConfigurations
import com.corrot.db.KwiatonomousDatabase
import com.corrot.db.data.model.DeviceConfiguration
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class DeviceConfigurationDaoImpl(private val database: KwiatonomousDatabase) : DeviceConfigurationDao {

    init {
        transaction(database.db) {
            SchemaUtils.create(DevicesConfigurations)
        }
    }

    override fun getDeviceConfiguration(deviceID: String): DeviceConfiguration? =
        transaction(database.db) {
            DevicesConfigurations.select { DevicesConfigurations.deviceID eq deviceID }.map {
                DeviceConfiguration(
                    deviceID = it[DevicesConfigurations.deviceID],
                    sleepTimeMinutes = it[DevicesConfigurations.sleepTimeMinutes],
                    wateringOn = it[DevicesConfigurations.wateringOn],
                    wateringIntervalDays = it[DevicesConfigurations.wateringIntervalDays],
                    wateringAmount = it[DevicesConfigurations.wateringAmount]
                )
            }.singleOrNull()
        }

    override fun createDeviceConfiguration(
        deviceID: String,
        sleepTimeMinutes: Int,
        wateringOn: Boolean,
        wateringIntervalDays: Int,
        wateringAmount: Int
    ): Unit =
        transaction(database.db) {
            DevicesConfigurations.insert {
                it[DevicesConfigurations.deviceID] = deviceID
                it[DevicesConfigurations.sleepTimeMinutes] = sleepTimeMinutes
                it[DevicesConfigurations.wateringOn] = wateringOn
                it[DevicesConfigurations.wateringIntervalDays] = wateringIntervalDays
                it[DevicesConfigurations.wateringAmount] = wateringAmount
            }
        }

    override fun updateDeviceConfiguration(deviceID: String, deviceConfiguration: DeviceConfiguration): Unit =
        transaction(database.db) {
            DevicesConfigurations.update(where = { Devices.deviceID eq deviceID }, body = {
                it[DevicesConfigurations.sleepTimeMinutes] = deviceConfiguration.sleepTimeMinutes
                it[DevicesConfigurations.wateringOn] = deviceConfiguration.wateringOn
                it[DevicesConfigurations.wateringIntervalDays] = deviceConfiguration.wateringIntervalDays
                it[DevicesConfigurations.wateringAmount] = deviceConfiguration.wateringAmount
            })
        }
}