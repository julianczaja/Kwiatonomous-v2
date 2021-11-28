package com.corrot.plugins

import com.corrot.db.data.dao.DeviceConfigurationDao
import com.corrot.db.data.dao.DeviceDao
import com.corrot.db.data.dao.DeviceUpdateDao
import com.corrot.routes.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*


fun Application.configureRouting(
    deviceDao: DeviceDao,
    deviceUpdateDao: DeviceUpdateDao,
    deviceConfigurationDao: DeviceConfigurationDao
) {
    routing {
        // get("/kwiatonomous/all")
        getAllKwiatonomousDevices(deviceDao)

        // get("/kwiatonomous/{id}")
        getKwiatonomousDevice(deviceDao)

        // get("/kwiatonomous/{id}/nextwatering")
        getKwiatonomousDeviceNextWatering(deviceDao)

        // get("/kwiatonomous/{id}/updates")
        getAllKwiatonomousDeviceUpdates(deviceUpdateDao)

        // get("/kwiatonomous/{id}/configuration")
        getKwiatonomousDeviceConfiguration(deviceConfigurationDao)

        // post("/kwiatonomous/{id}/updates")
        addKwiatonomousDeviceUpdate(deviceDao, deviceUpdateDao)

        // post("/kwiatonomous/{id}/configuration")
        updateKwiatonomousDeviceConfiguration(deviceConfigurationDao)
    }
}
