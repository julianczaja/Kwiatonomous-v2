package com.corrot.routes

import com.corrot.db.data.dao.DeviceDao
import com.corrot.db.data.dao.UserDao
import com.corrot.db.data.model.UserDevice
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*

fun Route.updateUserAddedDevicesIds(path: String, userDao: UserDao, deviceDao: DeviceDao) {
    post(path) {
        val userId = call.principal<UserIdPrincipal>()?.name
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "Not authenticated")
            return@post
        }

        val user = userDao.getUser(userId)
        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, "User doesn't exist")
            return@post
        }

        try {
            val newUserDevices = call.receive<List<UserDevice>>()

            newUserDevices.forEach {
                if (deviceDao.getDevice(it.deviceId) == null) {
                    call.respond(HttpStatusCode.BadRequest, "Device ${it.deviceId} doesn't exist")
                    return@post
                }
            }

            userDao.updateUserDevices(userId, newUserDevices)
            call.response.status(HttpStatusCode.OK)
        } catch (e: Exception) {
            e.printStack()
            call.response.status(HttpStatusCode.BadRequest)
        }
    }
}
