package com.corrot.routes

import com.corrot.db.data.dao.DeviceConfigurationDao
import com.corrot.db.data.dao.UserDao
import com.corrot.db.data.dto.DeviceConfigurationDto
import com.corrot.utils.toInt
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getKwiatonomousDeviceConfiguration(
    path: String,
    userDao: UserDao,
    deviceConfigurationDao: DeviceConfigurationDao
) {
    get(path) {
        val userId = call.principal<UserIdPrincipal>()?.name
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "Not authenticated")
            return@get
        }

        val deviceId = call.parameters["deviceId"]
        if (deviceId == null) {
            call.respond(HttpStatusCode.BadRequest, "Kwiatonomous device id can't be null!")
            return@get
        }

        val user = userDao.getUser(userId)
        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, "User doesn't exist")
            return@get
        }

        if (user.devices.find { it.deviceId == deviceId } == null) {
            call.respond(HttpStatusCode.BadRequest, "User doesn't have this device added")
            return@get
        }

        val deviceConfiguration = deviceConfigurationDao.getDeviceConfiguration(deviceId)
        if (deviceConfiguration == null) {
            call.respond(HttpStatusCode.NotFound, "Can't find Kwiatonomous configuration for device of id: $deviceId")
        } else {
            call.respond(
                HttpStatusCode.OK, DeviceConfigurationDto(
                    sleepTimeMinutes = deviceConfiguration.sleepTimeMinutes,
                    timeZoneOffset = deviceConfiguration.timeZoneOffset,
                    wateringOn = deviceConfiguration.wateringOn.toInt(),
                    wateringIntervalDays = deviceConfiguration.wateringIntervalDays,
                    wateringAmount = deviceConfiguration.wateringAmount,
                    wateringTime = deviceConfiguration.wateringTime
                )
            )
        }
    }
}