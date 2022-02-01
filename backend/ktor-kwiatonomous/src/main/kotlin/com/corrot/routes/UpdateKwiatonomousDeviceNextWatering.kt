package com.corrot.routes

import com.corrot.db.data.dao.DeviceDao
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.utils.io.*

fun Route.updateKwiatonomousDeviceNextWatering(deviceDao: DeviceDao) {
    post("/kwiatonomous/{id}/nextwatering") {
        val id = call.parameters["id"]

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Kwiatonomous device id can't be null!")
            return@post
        }

        if (deviceDao.getDevice(id) == null) {
            call.respond(HttpStatusCode.NotFound, "Can't find Kwiatonomous device of id: $id")
            return@post
        }

        try {
            call.receive<Long>().let { newNextWateringTime ->
                println("New next watering time: $newNextWateringTime")
                deviceDao.updateNextWatering(id, newNextWateringTime)
            }
            call.response.status(HttpStatusCode.OK)
        } catch (e: Exception) {
            e.printStack()
            call.response.status(HttpStatusCode.BadRequest)
        }
    }
}