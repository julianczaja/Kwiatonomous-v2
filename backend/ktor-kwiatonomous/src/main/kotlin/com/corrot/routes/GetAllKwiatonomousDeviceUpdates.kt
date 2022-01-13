package com.corrot.routes

import com.corrot.db.data.dao.DeviceUpdateDao
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.getAllKwiatonomousDeviceUpdates(deviceUpdateDao: DeviceUpdateDao) {
    get("/kwiatonomous/{id}/updates") {
        val deviceId = call.parameters["id"]

        if (deviceId == null) {
            call.respond(HttpStatusCode.BadRequest, "Kwiatonomous device id can't be null!")
            return@get
        }

        val limit = call.request.queryParameters["limit"]?.toIntOrNull()
        val from = call.request.queryParameters["from"]?.toLongOrNull()
        val to = call.request.queryParameters["to"]?.toLongOrNull()

        val deviceUpdates = deviceUpdateDao.getAllDeviceUpdates(
            deviceId = deviceId,
            limit = limit,
            fromTimestamp = from,
            toTimestamp = to
        )

        if (deviceUpdates.isEmpty()) {
            call.respond(
                HttpStatusCode.BadRequest, "Can't find Kwiatonomous device updates of id: '$deviceId'.\n" +
                        "Parameters given: limit=$limit, from=$from, to=$to"
            )
        } else {
            call.respond(HttpStatusCode.OK, deviceUpdates)
        }
    }
}
