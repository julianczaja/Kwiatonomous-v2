package com.corrot.routes

import com.corrot.db.data.dao.DeviceDao
import com.corrot.db.data.dao.DeviceUpdateDao
import com.corrot.db.data.dto.DeviceUpdateDto
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.addKwiatonomousDeviceUpdate(path: String, deviceDao: DeviceDao, deviceUpdateDao: DeviceUpdateDao) {
    post(path) {
        val id = call.parameters["id"]

        if (id == null) {
            call.respond(HttpStatusCode.NotFound, "Kwiatonomous device id can't be null!")
            return@post
        }

        if (deviceDao.getDevice(id) == null) {
            deviceDao.createDevice(id)
        }

        try {
            call.receive<DeviceUpdateDto>().let { deviceUpdate ->
                println(deviceUpdate)
                deviceUpdateDao.createDeviceUpdate(
                    deviceId = id,
                    timestamp = deviceUpdate.timestamp,
                    batteryLevel = deviceUpdate.batteryLevel,
                    batteryVoltage = deviceUpdate.batteryVoltage,
                    temperature = deviceUpdate.temperature,
                    humidity = deviceUpdate.humidity
                )

                deviceDao.updateDevice(id, deviceUpdate.timestamp)
            }
            call.response.status(HttpStatusCode.OK)
        } catch (e: Exception) {
            e.printStackTrace()
            call.response.status(HttpStatusCode.InternalServerError)
        }
    }
}
