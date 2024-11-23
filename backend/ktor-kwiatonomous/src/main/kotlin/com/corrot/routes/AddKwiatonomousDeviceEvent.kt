package com.corrot.routes

import com.corrot.db.data.dao.DeviceEventDao
import com.corrot.db.data.dao.UserDao
import com.corrot.db.data.dto.DeviceEventDto
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.addKwiatonomousDeviceEvent(path: String, userDao: UserDao, deviceEventDao: DeviceEventDao) {
    post(path) {
        val userId = call.principal<UserIdPrincipal>()?.name
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "Not authenticated")
            return@post
        }

        val deviceId = call.parameters["deviceId"]
        if (deviceId == null) {
            call.respond(HttpStatusCode.BadRequest, "Kwiatonomous device id can't be null!")
            return@post
        }

        val user = userDao.getUser(userId)
        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, "User doesn't exist")
            return@post
        }

        if (user.devices.find { it.deviceId == deviceId } == null) {
            call.respond(HttpStatusCode.BadRequest, "User doesn't have this device added")
            return@post
        }

        try {
            call.receive<DeviceEventDto>().let { deviceEventDto ->
                deviceEventDao.createDeviceEvent(
                    deviceId,
                    deviceEventDto.timestamp,
                    deviceEventDto.type,
                    deviceEventDto.data,
                )
            }
            call.response.status(HttpStatusCode.OK)
        } catch (e: Exception) {
            e.printStackTrace()
            call.response.status(HttpStatusCode.InternalServerError)
        }
    }
}
