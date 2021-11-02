package com.corrot.routes

import com.corrot.db.data.dao.DeviceDao
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.getKwiatonomousDevice(deviceDao: DeviceDao) {
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