package com.corrot.routes

import com.corrot.db.data.dao.DeviceDao
import com.corrot.db.data.dao.DeviceEventDao
import com.corrot.db.data.dto.DeviceEventDto
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.addKwiatonomousDeviceEventESP(path: String, deviceDao: DeviceDao, deviceEventDao: DeviceEventDao) {
    post(path) {
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
            call.receive<DeviceEventDto>().let { deviceEventDto ->
                println("New device event: $deviceEventDto")
                deviceEventDao.createDeviceEvent(id, deviceEventDto.timestamp, deviceEventDto.type, deviceEventDto.data)
            }
            call.response.status(HttpStatusCode.OK)
        } catch (e: Exception) {
            e.printStackTrace()
            call.response.status(HttpStatusCode.BadRequest)
        }
    }
}