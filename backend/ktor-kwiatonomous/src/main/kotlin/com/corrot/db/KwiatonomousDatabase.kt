package com.corrot.db

import com.corrot.Constants.DEBUG_MODE
import com.corrot.db.data.dao.DeviceConfigurationDao
import com.corrot.db.data.dao.DeviceDao
import com.corrot.db.data.dao.DeviceUpdateDao
import com.corrot.db.data.model.DeviceConfiguration
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class KwiatonomousDatabase {

    val db: Database = Database.connect(
        url = if (DEBUG_MODE)
            "jdbc:h2:mem:test_mem;DB_CLOSE_DELAY=-1;"
        else
            "jdbc:h2:file:${System.getProperty("user.home")}/kwiatonomous",
        driver = "org.h2.Driver"
    )

    fun isConnected() = try {
        transaction { !connection.isClosed }
    } catch (e: Exception) {
        false
    }
}

fun populateDatabase(
    deviceId: String,
    deviceDao: DeviceDao,
    deviceUpdatesDao: DeviceUpdateDao,
    deviceConfigurationDao: DeviceConfigurationDao
) {
    val currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

    // Add device
    deviceDao.createDevice(deviceId, birthday = currentTime)

    // Update next watering
    deviceDao.updateNextWatering(deviceId, currentTime + 24L * 3600L)

    // Update device configuration
    deviceConfigurationDao.updateDeviceConfiguration(
        DeviceConfiguration(
            deviceId,
            30,
            true,
            1,
            250,
            "10:30"
        )
    )

    // Add some device updates
    val updatesCount = 240
    repeat(updatesCount) { i ->
        deviceUpdatesDao.createDeviceUpdate(
            deviceId,
            timestamp = currentTime - (updatesCount - i) * (3600L * 3L),
            batteryLevel = 50 - ((Random().nextFloat() - 1f) / 50f).toInt(),
            batteryVoltage = 4.1f - (i.toFloat() / updatesCount) + (Random().nextFloat() / 10f),
            temperature = 22.3f - (Random().nextFloat() - 1f),
            humidity = 55.4f - (Random().nextFloat() - 1f)
        )
    }
}