package com.corrot.db

import com.corrot.Constants.DEBUG_MODE
import com.corrot.db.data.dao.DeviceConfigurationDao
import com.corrot.db.data.dao.DeviceDao
import com.corrot.db.data.dao.DeviceUpdateDao
import com.corrot.db.data.model.DeviceConfiguration
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.DriverManager
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class KwiatonomousDatabase {

    private val dbPath =
        if (DEBUG_MODE) "jdbc:sqlite:file:test?mode=memory&cache=shared"
        else "jdbc:sqlite:file:${System.getProperty("user.home")}/kwiatonomous.sqlite"

    // Hack to not close in memory database
    // https://github.com/JetBrains/Exposed/issues/726#issuecomment-932202379
    private val keepAliveConnection =
        if (DEBUG_MODE) DriverManager.getConnection(dbPath) else null

    val db: Database = Database.connect(dbPath, "org.sqlite.JDBC")

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
            1,
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