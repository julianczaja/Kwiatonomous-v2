package com.corrot.routes

import com.corrot.db.data.dao.DeviceDao
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.updateKwiatonomousDeviceNextWatering(deviceDao: DeviceDao) {
    post("/kwiatonomous/{id}/nextwatering") {
        val id = call.parameters["id"]

        if (id == null) {
            call.respondText("Kwiatonomous device id can't be null!")
            return@post
        }

        if (deviceDao.getDevice(id) == null) {
            call.respondText("Can't find Kwiatonomous device of id: $id")
            return@post
        }

        call.receive<Long>().let { newNextWateringTime ->
            println("New next watering time: $newNextWateringTime")
            deviceDao.updateNextWatering(id, newNextWateringTime)
            call.response.status(HttpStatusCode.OK)
        }
    }
}