package com.corrot.db

import com.corrot.Constants.DEBUG_MODE
import com.corrot.calculateHA1
import com.corrot.db.data.dao.DeviceConfigurationDao
import com.corrot.db.data.dao.DeviceDao
import com.corrot.db.data.dao.DeviceUpdateDao
import com.corrot.db.data.dao.UserDao
import com.corrot.db.data.model.DeviceConfiguration
import com.corrot.db.data.model.UserDevice
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.DriverManager
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class KwiatonomousDatabase {

        private val debugDbPath = "jdbc:sqlite:file:test?mode=memory&cache=shared"
//    private val debugDbPath = "jdbc:sqlite:file:kwiatonomous.sqlite"

    private val releaseDbPath = "jdbc:sqlite:file:${System.getProperty("user.home")}/kwiatonomous.sqlite"

    private val dbPath = if (DEBUG_MODE) debugDbPath else releaseDbPath

    // Hack to prevent closing in memory database
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
    userDao: UserDao,
    deviceDao: DeviceDao,
    deviceUpdatesDao: DeviceUpdateDao,
    deviceConfigurationDao: DeviceConfigurationDao
) {
    println("Populating database...")

    listOf("test_id_01", "test_id_02", "test_id_03").forEach {
        populateDevice(it, deviceDao, deviceUpdatesDao, deviceConfigurationDao)
    }
    listOf("testid", "testid2").forEach {
        populateUser(it, "password", userDao)
    }

    println("Populating database DONE")
}

fun populateUser(
    userId: String,
    password: String,
    userDao: UserDao
) {
    val ha1 = calculateHA1(userId, password)
    userDao.createUser(userId, ha1)
    userDao.updateUserDevices(
        userId, listOf(
            UserDevice(
                deviceId = "test_id_01",
                deviceName = "KWIATEK",
                deviceImageId = 2131165287,
                isFavourite = false,
                notificationsOn = true
            )
        )
    )
}

fun populateDevice(
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
            deviceId = deviceId,
            sleepTimeMinutes = 30,
            timeZoneOffset = 1,
            wateringOn = true,
            wateringIntervalDays = 1,
            wateringAmount = 250,
            wateringTime = "10:30"
        )
    )

    // Add some device updates
    val updatesCount = 100
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