package com.corrot.routes

import com.corrot.db.data.dao.DeviceConfigurationDao
import com.corrot.db.data.dao.UserDao
import com.corrot.db.data.dto.DeviceConfigurationDto
import com.corrot.db.data.model.DeviceConfiguration
import com.corrot.utils.toBoolean
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.updateKwiatonomousDeviceConfiguration(
    path: String,
    userDao: UserDao,
    deviceConfigurationDao: DeviceConfigurationDao
) {
    post(path) {
        val userId = call.principal<UserIdPrincipal>()?.name
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "Not authenticated")
            return@post
        }

        val deviceId = call.parameters["deviceId"]
        if (deviceId == null) {
            call.respond(HttpStatusCode.BadRequest, "Kwiatonomous device id can't be null!")
            return@post
        }

        val user = userDao.getUser(userId)
        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, "User doesn't exist")
            return@post
        }

        if (user.devices.find { it.deviceId == deviceId } == null) {
            call.respond(HttpStatusCode.BadRequest, "User doesn't have this device added")
            return@post
        }

        if (deviceConfigurationDao.getDeviceConfiguration(deviceId) == null) {
            call.respond(HttpStatusCode.NotFound, "Can't find Kwiatonomous configuration for device of id: $deviceId")
            return@post
        }

        try {
            call.receive<DeviceConfigurationDto>().let { deviceConfigurationDto ->
                val newDeviceConfiguration = DeviceConfiguration(
                    deviceId = deviceId,
                    sleepTimeMinutes = deviceConfigurationDto.sleepTimeMinutes,
                    timeZoneOffset = deviceConfigurationDto.timeZoneOffset,
                    wateringOn = deviceConfigurationDto.wateringOn.toBoolean(),
                    wateringIntervalDays = deviceConfigurationDto.wateringIntervalDays,
                    wateringAmount = deviceConfigurationDto.wateringAmount,
                    wateringTime = deviceConfigurationDto.wateringTime
                )
                deviceConfigurationDao.updateDeviceConfiguration(newDeviceConfiguration)
            }
            call.response.status(HttpStatusCode.OK)
        } catch (e: Exception) {
            e.printStackTrace()
            call.response.status(HttpStatusCode.InternalServerError)
        }
    }
}