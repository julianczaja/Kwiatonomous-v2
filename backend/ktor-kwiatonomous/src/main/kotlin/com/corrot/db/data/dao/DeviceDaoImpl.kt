package com.corrot.db.data.dao

import com.corrot.db.Devices
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
                    lastUpdate = it[Devices.lastUpdate]
                )
            }
        }

    override fun getDevice(deviceID: String): Device? =
        transaction(database.db) {
            Devices.select { Devices.deviceID eq deviceID }.map {
                Device(
                    deviceID = it[Devices.deviceID],
                    birthday = it[Devices.birthday],
                    lastUpdate = it[Devices.lastUpdate]
                )
            }.singleOrNull()
        }

    override fun createDevice(deviceID: String, birthday: Long?): Unit =
        transaction(database.db) {
            Devices.insert {
                it[Devices.deviceID] = deviceID
                it[Devices.birthday] = birthday ?: TimeUtils.getCurrentTimestamp()
                it[lastUpdate] = TimeUtils.getCurrentTimestamp()
            }
        }

    override fun updateDevice(deviceID: String, lastUpdate: Long): Unit =
        transaction(database.db) {
            Devices.update(where = { Devices.deviceID eq deviceID }, body = {
                it[Devices.lastUpdate] = lastUpdate
            })
        }

    override fun deleteDevice(deviceID: String): Unit = transaction(database.db) {
        Devices.deleteWhere { Devices.deviceID eq deviceID }
    }
}