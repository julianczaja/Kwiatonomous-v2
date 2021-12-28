package com.corrot.routes

import com.corrot.db.data.dao.DeviceConfigurationDao
import com.corrot.db.data.dto.DeviceConfigurationDto
import com.corrot.utils.toInt
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.getKwiatonomousDeviceConfiguration(deviceConfigurationDao: DeviceConfigurationDao) {
    get("/kwiatonomous/{id}/configuration") {
        val id = call.parameters["id"]

        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Kwiatonomous device id can't be null!")
            return@get
        }

        val deviceConfiguration = deviceConfigurationDao.getDeviceConfiguration(id)

        if (deviceConfiguration == null) {
            call.respond(HttpStatusCode.BadRequest, "Can't find Kwiatonomous configuration for device of id: $id")

        } else {
            call.respond(
                HttpStatusCode.OK, DeviceConfigurationDto(
                    deviceConfiguration.sleepTimeMinutes,
                    deviceConfiguration.wateringOn.toInt(),
                    deviceConfiguration.wateringIntervalDays,
                    deviceConfiguration.wateringAmount,
                    deviceConfiguration.wateringTime
                )
            )
        }
    }
}