package com.corrot.db.data.dao

import com.corrot.Constants.DEFAULT_SLEEP_TIME_MINUTES
import com.corrot.Constants.DEFAULT_TIME_ZONE_OFFSET
import com.corrot.Constants.DEFAULT_WATERING_AMOUNT
import com.corrot.Constants.DEFAULT_WATERING_INTERVAL_DAYS
import com.corrot.Constants.DEFAULT_WATERING_ON
import com.corrot.Constants.DEFAULT_WATERING_TIME
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
            SchemaUtils.createMissingTablesAndColumns(Devices)
            SchemaUtils.create(Devices)
        }
    }

    override fun getAllDevices(): List<Device> =
        transaction(database.db) {
            Devices.selectAll().map {
                Device(
                    deviceId = it[Devices.deviceId],
                    birthday = it[Devices.birthday],
                    lastPumpCleaning = it[Devices.lastPumpCleaning],
                    lastUpdate = it[Devices.lastUpdate],
                    nextWatering = it[Devices.nextWatering]
                )
            }
        }

    override fun getDevice(deviceId: String): Device? =
        transaction(database.db) {
            Devices.select { Devices.deviceId eq deviceId }.map {
                Device(
                    deviceId = it[Devices.deviceId],
                    birthday = it[Devices.birthday],
                    lastPumpCleaning = it[Devices.lastPumpCleaning],
                    lastUpdate = it[Devices.lastUpdate],
                    nextWatering = it[Devices.nextWatering]
                )
            }.singleOrNull()
        }

    override fun createDevice(deviceId: String, birthday: Long?) {
        transaction(database.db) {
            Devices.insert {
                it[Devices.deviceId] = deviceId
                it[Devices.birthday] = birthday ?: TimeUtils.getCurrentTimestamp()
                it[Devices.lastPumpCleaning] = TimeUtils.getCurrentTimestamp()
                it[Devices.lastUpdate] = TimeUtils.getCurrentTimestamp()
                it[Devices.nextWatering] = 4294967294; // max unsigned long value for ESP8266
            }
            // Add default device configuration
            DevicesConfigurations.insert {
                it[DevicesConfigurations.deviceId] = deviceId
                it[DevicesConfigurations.sleepTimeMinutes] = DEFAULT_SLEEP_TIME_MINUTES
                it[DevicesConfigurations.timeZoneOffset] = DEFAULT_TIME_ZONE_OFFSET
                it[DevicesConfigurations.wateringOn] = DEFAULT_WATERING_ON
                it[DevicesConfigurations.wateringIntervalDays] = DEFAULT_WATERING_INTERVAL_DAYS
                it[DevicesConfigurations.wateringAmount] = DEFAULT_WATERING_AMOUNT
                it[DevicesConfigurations.wateringTime] = DEFAULT_WATERING_TIME
            }
        }
    }

    override fun updateDevice(deviceId: String, lastUpdate: Long): Unit =
        transaction(database.db) {
            Devices.update(where = { Devices.deviceId eq deviceId }, body = {
                it[Devices.lastUpdate] = lastUpdate
            })
        }

    override fun updateNextWatering(deviceId: String, newNextWateringTime: Long) {
        transaction(database.db) {
            Devices.update(where = { Devices.deviceId eq deviceId }, body = {
                it[Devices.nextWatering] = newNextWateringTime
            })
        }
    }

    override fun updateLastPumpCleaning(deviceId: String, newLastPumpCleaningTime: Long) {
        transaction(database.db) {
            Devices.update(where = { Devices.deviceId eq deviceId }, body = {
                it[Devices.lastPumpCleaning] = newLastPumpCleaningTime
            })
        }
    }

    override fun deleteDevice(deviceId: String): Unit = transaction(database.db) {
        Devices.deleteWhere { Devices.deviceId eq deviceId }
    }
}