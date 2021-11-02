package com.corrot.plugins

import com.corrot.db.data.dao.DeviceDao
import com.corrot.db.data.dao.DeviceUpdateDao
import com.corrot.routes.addKwiatonomousDeviceUpdate
import com.corrot.routes.getAllKwiatonomousDeviceUpdates
import com.corrot.routes.getAllKwiatonomousDevices
import com.corrot.routes.getKwiatonomousDevice
import io.ktor.application.*
import io.ktor.routing.*


fun Application.configureRouting(deviceDao: DeviceDao, deviceUpdateDao: DeviceUpdateDao) {
    routing {
        getAllKwiatonomousDevices(deviceDao)
        getKwiatonomousDevice(deviceDao)
        getAllKwiatonomousDeviceUpdates(deviceUpdateDao)
        addKwiatonomousDeviceUpdate(deviceDao, deviceUpdateDao)
    }
}
