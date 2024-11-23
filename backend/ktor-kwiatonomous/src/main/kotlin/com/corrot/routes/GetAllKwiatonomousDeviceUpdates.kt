package com.corrot.routes

import com.corrot.db.data.dao.DeviceUpdateDao
import com.corrot.db.data.dao.UserDao
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getAllKwiatonomousDeviceUpdates(path: String, userDao: UserDao, deviceUpdateDao: DeviceUpdateDao) {
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

        val deviceUpdates = deviceUpdateDao.getAllDeviceUpdates(
            deviceId = deviceId,
            limit = call.request.queryParameters["limit"]?.toIntOrNull(),
            fromTimestamp = call.request.queryParameters["from"]?.toLongOrNull(),
            toTimestamp = call.request.queryParameters["to"]?.toLongOrNull()
        )

        call.respond(HttpStatusCode.OK, deviceUpdates)
    }
}
