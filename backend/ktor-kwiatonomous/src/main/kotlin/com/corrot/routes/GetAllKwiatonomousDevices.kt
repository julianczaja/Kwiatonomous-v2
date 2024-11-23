package com.corrot.routes

import com.corrot.db.data.dao.DeviceDao
import com.corrot.db.data.dao.UserDao
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getAllKwiatonomousDevices(path: String, userDao: UserDao, deviceDao: DeviceDao) {
    get(path) {
        val userId = call.principal<UserIdPrincipal>()?.name
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "Not authenticated")
            return@get
        }

        val user = userDao.getUser(userId)
        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, "User doesn't exist")
            return@get
        }

        val allDevices = user.devices.map { userDevice ->
            val device = deviceDao.getDevice(userDevice.deviceId)
            if (device == null) {
                call.respond(HttpStatusCode.BadRequest, "Device ${userDevice.deviceId} doesn't exist")
                return@get
            }
            return@map device
        }
        call.respond(HttpStatusCode.OK, allDevices)
    }
}
