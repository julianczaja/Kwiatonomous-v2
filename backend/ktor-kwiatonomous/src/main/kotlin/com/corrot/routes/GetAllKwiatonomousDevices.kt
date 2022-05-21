package com.corrot.routes

import com.corrot.db.data.dao.DeviceDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getAllKwiatonomousDevices(deviceDao: DeviceDao) {
    get("/kwiatonomous/all") {
        call.respond(HttpStatusCode.OK, deviceDao.getAllDevices())
    }
}
