package com.corrot.routes

import com.corrot.db.data.dao.DeviceConfigurationDao
import com.corrot.db.data.dto.DeviceConfigurationDto
import com.corrot.utils.toBoolean
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*


fun Route.updateKwiatonomousDeviceConfiguration(deviceConfigurationDao: DeviceConfigurationDao) {
    post("/kwiatonomous/{id}/configuration") {

        val deviceId = call.parameters["id"]

        if (deviceId == null) {
            call.respondText("Kwiatonomous device id can't be null!")
            return@post
        }

        if (deviceConfigurationDao.getDeviceConfiguration(deviceId) == null) {
            call.respondText("Can't find Kwiatonomous configuration for device of id: $deviceId")
            return@post
        }

        call.receive<DeviceConfigurationDto>().let { deviceConfiguration ->
            println(deviceConfiguration)
            deviceConfigurationDao.createDeviceConfiguration(
                deviceId,
                deviceConfiguration.sleepTimeMinutes,
                deviceConfiguration.wateringOn.toBoolean(),
                deviceConfiguration.wateringIntervalDays,
                deviceConfiguration.wateringAmount
            )
        }
    }
}