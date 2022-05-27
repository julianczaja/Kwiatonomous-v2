package com.corrot.routes

import com.corrot.db.data.dao.DeviceDao
import com.corrot.db.data.dao.UserDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.getKwiatonomousDeviceNextWatering(path: String, userDao: UserDao, deviceDao: DeviceDao) {
    get(path) {
        val userId = call.principal<UserIdPrincipal>()?.name
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "Not authenticated")
            return@get
        }

        val deviceId = call.parameters["deviceId"]
        if (deviceId == null) {
            call.respond(HttpStatusCode.BadRequest, "Kwiatonomous device id can't be null")
            return@get
        }

        val user = userDao.getUser(userId)
        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, "User doesn't exist")
            return@get
        }

        if (user.devices.find { it.deviceId == deviceId } == null) {
            call.respond(HttpStatusCode.BadRequest, "User doesn't have this device added")
            return@get
        }

        val device = deviceDao.getDevice(deviceId)
        if (device != null) {
            call.respond(HttpStatusCode.OK, device.nextWatering)
        } else {
            call.respond(HttpStatusCode.NotFound, "Can't find Kwiatonomous device of id: $deviceId")
        }
    }
}