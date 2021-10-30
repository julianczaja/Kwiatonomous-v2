package com.corrot

import com.corrot.db.data.dao.DeviceDaoImpl
import com.corrot.db.data.dao.DeviceUpdateDaoImpl
import com.corrot.plugins.configureMonitoring
import com.corrot.plugins.configureRouting
import com.corrot.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database

fun main() {
    // embeddedServer(Netty, port = 8015, host = "192.168.43.17") {
    embeddedServer(Netty, port = 20188, host = "192.168.1.188") {

        configureSerialization()
        configureMonitoring()

        // val database = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
        val database = Database.connect(
            url = "jdbc:h2:file:${System.getProperty("user.home")}/kwiatonomous",
            driver = "org.h2.Driver"
        )
        val deviceDao = DeviceDaoImpl(database)
        val deviceUpdatesDao = DeviceUpdateDaoImpl(database)
        deviceDao.init()
        deviceUpdatesDao.init()

        configureRouting(deviceDao, deviceUpdatesDao)

    }.start(wait = true)
}