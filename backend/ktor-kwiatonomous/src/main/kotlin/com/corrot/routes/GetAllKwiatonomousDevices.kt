package com.corrot.routes

import com.corrot.db.data.dao.DeviceDao
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.getAllKwiatonomousDevices(deviceDao: DeviceDao) {
    get("/kwiatonomous/all") {
        val devices = deviceDao.getAllDevices()

        if (devices.isEmpty()) {
            call.respondText("There are no Kwiatonomous devices added :(")
        } else {
            call.respond(devices)
        }
    }
}