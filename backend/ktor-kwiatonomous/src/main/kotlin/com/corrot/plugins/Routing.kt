package com.corrot.plugins

import com.corrot.db.data.dao.DeviceDao
import com.corrot.db.data.dao.DeviceUpdateDao
import com.corrot.db.data.model.DeviceUpdate
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting(deviceDao: DeviceDao, deviceUpdateDao: DeviceUpdateDao) {
    routing {
        kwiatonomousRoot()
        getAllKwiatonomousDevices(deviceDao)
        getKwiatonomousDevice(deviceDao)
        getAllKwiatonomousDeviceUpdates(deviceUpdateDao)
        addKwiatonomousDeviceUpdate(deviceUpdateDao)
    }
}

private fun Routing.kwiatonomousRoot() {
    get("/kwiatonomous") {
        call.respondText("Welcome to kwiatonomous project!")
    }
}

private fun Routing.getAllKwiatonomousDevices(deviceDao: DeviceDao) {
    get("/kwiatonomous/all") {
        val devices = deviceDao.getAllDevices()

        if (devices.isEmpty()) {
            call.respondText("There are no Kwiatonomous devices added :(")
        } else {
            call.respond(devices)
        }
    }
}

private fun Routing.getKwiatonomousDevice(deviceDao: DeviceDao) {
    get("/kwiatonomous/{id}") {
        val id = call.parameters["id"]

        if (id != null) {
            val device = deviceDao.getDevice(id)

            if (device != null) {
                call.respond(device)
            } else {
                call.respondText("Can't find Kwiatonomous device of id: $id")
            }
        } else {
            call.respondText("Kwiatonomous device id can't be null!")
        }
    }
}

private fun Routing.getAllKwiatonomousDeviceUpdates(deviceUpdateDao: DeviceUpdateDao) {
    get("/kwiatonomous/{id}/updates") {
        val id = call.parameters["id"]

        if (id != null) {
            val limit = call.request.queryParameters["limit"]?.toIntOrNull()
            val deviceUpdates = if (limit != null) {
                deviceUpdateDao.getLastDeviceUpdates(id, limit)
            } else {
                deviceUpdateDao.getAllDeviceUpdates(id)
            }

            if (deviceUpdates.isEmpty()) {
                call.respondText("Can't find Kwiatonomous device updates of id: $id")
            } else {
                call.respond(deviceUpdates)
            }
        } else {
            call.respondText("Kwiatonomous device id can't be null!")
        }
    }
}

private fun Routing.addKwiatonomousDeviceUpdate(deviceUpdateDao: DeviceUpdateDao) {
    post("/kwiatonomous/{id}/updates") {
        val id = call.parameters["id"]

        if (id != null) {
            val deviceUpdate = call.receive<DeviceUpdate>()
            println("Received: >>deviceID: $id, deviceUpdate: $deviceUpdate<<")
            deviceUpdateDao.createDeviceUpdate(
                deviceID = id,
                deviceUpdate.timestamp,
                deviceUpdate.batteryLevel,
                deviceUpdate.batteryVoltage,
                deviceUpdate.temperature,
                deviceUpdate.humidity
            )
            call.response.status(HttpStatusCode.OK)
        } else {
            call.respondText("Kwiatonomous device id can't be null!")
        }
    }
}