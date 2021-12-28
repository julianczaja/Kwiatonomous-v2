package com.corrot.routes

import com.corrot.db.data.dao.DeviceDao
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*


fun Route.getKwiatonomousDeviceNextWatering(deviceDao: DeviceDao) {
    get("/kwiatonomous/{id}/nextwatering") {
        val id = call.parameters["id"]

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Kwiatonomous device id can't be null!")
            return@get
        }

        val device = deviceDao.getDevice(id)

        if (device != null) {
            call.respond(HttpStatusCode.OK, device.nextWatering)
        } else {
            call.respond(HttpStatusCode.BadRequest, "Can't find Kwiatonomous device of id: $id")
        }
    }
}