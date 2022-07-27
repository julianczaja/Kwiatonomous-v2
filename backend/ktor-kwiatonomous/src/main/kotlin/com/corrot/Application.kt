package com.corrot

import com.corrot.Constants.DEBUG_MODE
import com.corrot.db.KwiatonomousDatabase
import com.corrot.db.data.dao.DeviceConfigurationDao
import com.corrot.db.data.dao.DeviceDao
import com.corrot.db.data.dao.DeviceUpdateDao
import com.corrot.db.data.dao.UserDao
import com.corrot.db.populateDatabase
import com.corrot.plugins.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import org.koin.ktor.ext.inject


fun main() {
    embeddedServer(
        factory = Jetty,
         port = 8015,
         host = "192.168.43.195"
    ) {
        configureKoin()
        configureMonitoring()
        configureTemplating()
        configureSerialization()

        val database by inject<KwiatonomousDatabase>()
        val userDao by inject<UserDao>()
        val deviceDao by inject<DeviceDao>()
        val deviceUpdatesDao by inject<DeviceUpdateDao>()
        val deviceConfigurationDao by inject<DeviceConfigurationDao>()

        println("\n-------------------\nStarting DATABASE: ${database.db.url}\n-------------------\n")

        if (!database.isConnected()) {
            throw RuntimeException("Error during DB connection!")
        }

        configureSecurity(userDao)
        configureRouting(userDao, deviceDao, deviceUpdatesDao, deviceConfigurationDao)

        if (DEBUG_MODE) {
            populateDatabase(userDao, deviceDao, deviceUpdatesDao, deviceConfigurationDao)
        }

    }.start(wait = true)
}