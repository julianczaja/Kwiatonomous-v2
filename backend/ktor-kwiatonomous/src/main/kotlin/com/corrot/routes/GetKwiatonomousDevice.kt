package com.corrot.routes

import com.corrot.db.data.dao.DeviceDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getKwiatonomousDevice(deviceDao: DeviceDao) {
    get("/kwiatonomous/{id}") {
        val id = call.parameters["id"]

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Kwiatonomous device id can't be null!")
            return@get
        }

        val device = deviceDao.getDevice(id)

        if (device != null) {
            call.respond(HttpStatusCode.OK, device)
        } else {
            call.respond(HttpStatusCode.NotFound, "Can't find Kwiatonomous device of id: $id")
        }
    }
}