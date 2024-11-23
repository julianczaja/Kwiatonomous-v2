package com.corrot

import com.corrot.Constants.DEBUG_MODE
import com.corrot.db.KwiatonomousDatabase
import com.corrot.db.data.dao.*
import com.corrot.db.populateDatabase
import com.corrot.plugins.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.ktor.ext.inject


fun main() {
    embeddedServer(
        factory = Netty,
        host = Constants.BASE_URL,
        port = Constants.PORT
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
        val deviceEventDao by inject<DeviceEventDao>()

        println("\n-------------------\nStarting DATABASE: ${database.db.url}\n-------------------\n")

        if (!database.isConnected()) {
            throw RuntimeException("Error during DB connection!")
        }

        configureSecurity(userDao)
        configureRouting(userDao, deviceDao, deviceUpdatesDao, deviceConfigurationDao, deviceEventDao)

        if (DEBUG_MODE) {
            populateDatabase(userDao, deviceDao, deviceUpdatesDao, deviceConfigurationDao, deviceEventDao)
        }
    }.start(wait = true)
}