package com.corrot

import com.corrot.db.KwiatonomousDatabase
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
    embeddedServer(Netty, port = 8015, host = "192.168.43.17") {

        configureKoin()
        configureSerialization()
        configureMonitoring()

        val database by inject<KwiatonomousDatabase>()
        val deviceDao by inject<DeviceDao>()
        val deviceUpdatesDao by inject<DeviceUpdateDao>()

        if (!database.isConnected()) {
            throw RuntimeException("Error during DB connection!")
        }

        configureRouting(deviceDao, deviceUpdatesDao)

    }.start(wait = true)
}

