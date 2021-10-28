package com.corrot.db.data.dao

import com.corrot.db.Devices
import com.corrot.db.data.model.Device
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class DeviceDaoImpl(private val database: Database) : DeviceDao {

    override fun init() =
        transaction(database) {
            SchemaUtils.create(Devices)
        }

    override fun getAllDevices(): List<Device> =
        transaction(database) {
            Devices.selectAll().map {
                Device(
                    deviceID = it[Devices.deviceID],
                    birthday = it[Devices.birthday],
                    lastUpdate = it[Devices.lastUpdate]
                )
            }
        }

    override fun getDevice(deviceID: String): Device? =
        transaction(database) {
            Devices.select { Devices.deviceID eq deviceID }.map {
                Device(
                    deviceID = it[Devices.deviceID],
                    birthday = it[Devices.birthday],
                    lastUpdate = it[Devices.lastUpdate]
                )
            }.singleOrNull()
        }

    override fun createDevice(deviceID: String, birthday: Long?): Unit =
        transaction(database) {
            Devices.insert {
                it[Devices.deviceID] = deviceID
                it[Devices.birthday] = birthday ?: DateTime.now().millis
                it[lastUpdate] = DateTime.now().millis
            }
        }

    override fun updateDevice(deviceID: String, lastUpdate: Long): Unit =
        transaction(database) {
            Devices.update(where = { Devices.deviceID eq deviceID }, body = {
                it[Devices.lastUpdate] = lastUpdate
            })
        }

    override fun deleteDevice(deviceID: String): Unit = transaction(database) {
        Devices.deleteWhere { Devices.deviceID eq deviceID }
    }
}