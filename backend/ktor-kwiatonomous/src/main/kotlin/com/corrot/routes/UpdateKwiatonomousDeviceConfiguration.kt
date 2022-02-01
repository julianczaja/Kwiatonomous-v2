package com.corrot.routes

import com.corrot.db.data.dao.DeviceConfigurationDao
import com.corrot.db.data.dto.DeviceConfigurationDto
import com.corrot.db.data.model.DeviceConfiguration
import com.corrot.utils.toBoolean
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.utils.io.*


fun Route.updateKwiatonomousDeviceConfiguration(deviceConfigurationDao: DeviceConfigurationDao) {
    post("/kwiatonomous/{id}/configuration") {

        val deviceId = call.parameters["id"]

        if (deviceId == null) {
            call.respond(HttpStatusCode.BadRequest, "Kwiatonomous device id can't be null!")
            return@post
        }

        if (deviceConfigurationDao.getDeviceConfiguration(deviceId) == null) {
            call.respond(HttpStatusCode.NotFound, "Can't find Kwiatonomous configuration for device of id: $deviceId")
            return@post
        }

        try {
            call.receive<DeviceConfigurationDto>().let { deviceConfigurationDto ->
                val newDeviceConfiguration = DeviceConfiguration(
                    deviceId,
                    deviceConfigurationDto.sleepTimeMinutes,
                    deviceConfigurationDto.timeZoneOffset,
                    deviceConfigurationDto.wateringOn.toBoolean(),
                    deviceConfigurationDto.wateringIntervalDays,
                    deviceConfigurationDto.wateringAmount,
                    deviceConfigurationDto.wateringTime
                )
                println("New DeviceConfiguration: $newDeviceConfiguration")
                deviceConfigurationDao.updateDeviceConfiguration(newDeviceConfiguration)
            }
            call.response.status(HttpStatusCode.OK)
        } catch (e: Exception) {
            e.printStack()
            call.response.status(HttpStatusCode.InternalServerError)
        }
    }
}