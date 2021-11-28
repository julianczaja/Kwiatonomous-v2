package com.corrot.routes

import com.corrot.db.data.dao.DeviceDao
import com.corrot.db.data.dao.DeviceUpdateDao
import com.corrot.db.data.dto.DeviceUpdateDto
import com.corrot.utils.TimeUtils
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.addKwiatonomousDeviceUpdate(deviceDao: DeviceDao, deviceUpdateDao: DeviceUpdateDao) {
    post("/kwiatonomous/{id}/updates") {
        val id = call.parameters["id"]

        if (id != null) {
            if (deviceDao.getDevice(id) == null) {
                deviceDao.createDevice(id)
            }
            call.receive<DeviceUpdateDto>().let { deviceUpdate ->
                println(deviceUpdate)
                deviceUpdateDao.createDeviceUpdate(
                    id,
                    deviceUpdate.timestamp,
                    deviceUpdate.batteryLevel,
                    deviceUpdate.batteryVoltage,
                    deviceUpdate.temperature,
                    deviceUpdate.humidity,
                    deviceUpdate.nextWatering
                )
            }

            deviceDao.updateDevice(id, TimeUtils.getCurrentTimestamp())

            call.response.status(HttpStatusCode.OK)
        } else {
            call.respondText("Kwiatonomous device id can't be null!")
        }
    }
}