package com.corrot

import com.corrot.db.KwiatonomousDatabase
import com.corrot.db.data.dao.DeviceConfigurationDao
import com.corrot.db.data.dao.DeviceDao
import com.corrot.db.data.dao.DeviceUpdateDao
import com.corrot.plugins.configureKoin
import com.corrot.plugins.configureMonitoring
import com.corrot.plugins.configureRouting
import com.corrot.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.ktor.ext.inject


fun main() {
    embeddedServer(Netty, port = 8015, host = "192.168.43.195") {

        configureKoin()
        configureSerialization()
        configureMonitoring()

        val database by inject<KwiatonomousDatabase>()
        val deviceDao by inject<DeviceDao>()
        val deviceUpdatesDao by inject<DeviceUpdateDao>()
        val deviceConfigurationDao by inject<DeviceConfigurationDao>()

        if (!database.isConnected()) {
            throw RuntimeException("Error during DB connection!")
        }

        configureRouting(deviceDao, deviceUpdatesDao, deviceConfigurationDao)

        if (deviceDao.getDevice("testid") == null) {
            println("Adding test device")
            deviceDao.createDevice("testid")
            deviceUpdatesDao.createDeviceUpdate(
                "testid",
                timestamp = 123456,
                batteryLevel = 50,
                batteryVoltage = 3.56f,
                temperature = 22.3f,
                humidity = 55.4f
            )
        }

    }.start(wait = true)
}