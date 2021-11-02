package com.corrot.routes

import com.corrot.db.data.dao.DeviceUpdateDao
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.getAllKwiatonomousDeviceUpdates(deviceUpdateDao: DeviceUpdateDao) {
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
