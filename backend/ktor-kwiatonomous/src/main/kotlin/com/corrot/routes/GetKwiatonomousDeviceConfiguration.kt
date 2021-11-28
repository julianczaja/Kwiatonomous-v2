package com.corrot.routes

import com.corrot.db.data.dao.DeviceConfigurationDao
import com.corrot.db.data.dto.DeviceConfigurationDto
import com.corrot.utils.toInt
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.getKwiatonomousDeviceConfiguration(deviceConfigurationDao: DeviceConfigurationDao) {
    get("/kwiatonomous/{id}/configuration") {
        val id = call.parameters["id"]

        if (id != null) {
            val deviceConfiguration = deviceConfigurationDao.getDeviceConfiguration(id)

            if (deviceConfiguration != null) {
                call.respond(
                    DeviceConfigurationDto(
                        deviceConfiguration.sleepTimeMinutes,
                        deviceConfiguration.wateringOn.toInt(),
                        deviceConfiguration.wateringIntervalDays,
                        deviceConfiguration.wateringAmount
                    )
                )
            } else {
                call.respondText("Can't find Kwiatonomous configuration for device of id: $id")
            }
        } else {
            call.respondText("Kwiatonomous device id can't be null!")
        }
    }
}