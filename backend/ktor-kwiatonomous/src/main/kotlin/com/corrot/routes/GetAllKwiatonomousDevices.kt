package com.corrot.routes

import com.corrot.db.data.dao.DeviceDao
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.getAllKwiatonomousDevices(deviceDao: DeviceDao) {
    get("/kwiatonomous/all") {
        call.respond(HttpStatusCode.OK, deviceDao.getAllDevices())
    }
}
