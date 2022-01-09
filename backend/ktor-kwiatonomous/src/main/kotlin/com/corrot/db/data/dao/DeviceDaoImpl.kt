package com.corrot.db.data.dao

import com.corrot.db.Devices
import com.corrot.db.DevicesConfigurations
import com.corrot.db.KwiatonomousDatabase
import com.corrot.db.data.model.Device
import com.corrot.utils.TimeUtils
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class DeviceDaoImpl(private val database: KwiatonomousDatabase) : DeviceDao {

    init {
        transaction(database.db) {
            SchemaUtils.create(Devices)
        }
    }

    override fun getAllDevices(): List<Device> =
        transaction(database.db) {
            Devices.selectAll().map {
                Device(
                    deviceID = it[Devices.deviceID],
                    birthday = it[Devices.birthday],
                    lastUpdate = it[Devices.lastUpdate],
                    nextWatering = it[Devices.nextWatering]
                )
            }
        }

    override fun getDevice(deviceID: String): Device? =
        transaction(database.db) {
            Devices.select { Devices.deviceID eq deviceID }.map {
                Device(
                    deviceID = it[Devices.deviceID],
                    birthday = it[Devices.birthday],
                    lastUpdate = it[Devices.lastUpdate],
                    nextWatering = it[Devices.nextWatering]
                )
            }.singleOrNull()
        }

    override fun createDevice(deviceID: String, birthday: Long?) {
        transaction(database.db) {
            Devices.insert {
                it[Devices.deviceID] = deviceID
                it[Devices.birthday] = birthday ?: TimeUtils.getCurrentTimestamp()
                it[Devices.lastUpdate] = TimeUtils.getCurrentTimestamp()
                it[Devices.nextWatering] = 4294967294; // max unsigned long value for ESP8266
            }
            // Add default device configuration
            DevicesConfigurations.insert {
                it[DevicesConfigurations.deviceID] = deviceID
                it[DevicesConfigurations.sleepTimeMinutes] = 30
                it[DevicesConfigurations.timeZoneOffset] = 1
                it[DevicesConfigurations.wateringOn] = false
                it[DevicesConfigurations.wateringIntervalDays] = 2
                it[DevicesConfigurations.wateringAmount] = 50
                it[DevicesConfigurations.wateringTime] = "12:00" // HH:MM
            }
        }
    }

    override fun updateDevice(deviceID: String, lastUpdate: Long): Unit =
        transaction(database.db) {
            Devices.update(where = { Devices.deviceID eq deviceID }, body = {
                it[Devices.lastUpdate] = lastUpdate
            })
        }

    override fun updateNextWatering(deviceID: String, newNextWateringTime: Long) {
        transaction(database.db) {
            Devices.update(where = { Devices.deviceID eq deviceID }, body = {
                it[Devices.nextWatering] = newNextWateringTime
            })
        }
    }

    override fun deleteDevice(deviceID: String): Unit = transaction(database.db) {
        Devices.deleteWhere { Devices.deviceID eq deviceID }
    }
}