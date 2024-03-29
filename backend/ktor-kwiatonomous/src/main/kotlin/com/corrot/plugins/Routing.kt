package com.corrot.plugins

import com.corrot.Constants.KWIATONOMOUS_DIGEST_AUTH
import com.corrot.db.data.dao.*
import com.corrot.routes.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting(
    userDao: UserDao,
    deviceDao: DeviceDao,
    deviceUpdateDao: DeviceUpdateDao,
    deviceConfigurationDao: DeviceConfigurationDao,
    deviceEventDao: DeviceEventDao
) {
    routing {
        // ---------------------------------------- For Android ---------------------------------------- //
        // post
        registerNewAccount("/kwiatonomous/android/register", userDao)

        authenticate(KWIATONOMOUS_DIGEST_AUTH) {

            route("/kwiatonomous/status") {
                get {
                    call.respond(FreeMarkerContent("index.ftl", mapOf("devices" to deviceDao.getAllDevices())))
                }
            }

            // get
            getUser(
                "/kwiatonomous/android/user",
                userDao
            )

            // post
            updateUserAddedDevicesIds(
                "/kwiatonomous/android/user/devices",
                userDao, deviceDao
            )

            // get
            getAllKwiatonomousDevices(
                "/kwiatonomous/android/device/all",
                userDao, deviceDao
            )

            // get
            getKwiatonomousDevice(
                "/kwiatonomous/android/device/{deviceId}",
                userDao, deviceDao
            )

            // get
            getKwiatonomousDeviceNextWatering(
                "/kwiatonomous/android/device/{deviceId}/nextwatering",
                userDao, deviceDao
            )

            // post
            updateKwiatonomousDeviceNextWatering(
                "/kwiatonomous/android/device/{deviceId}/nextwatering",
                userDao, deviceDao
            )

            // post
            updateKwiatonomousDeviceLastPumpCleaning(
                "/kwiatonomous/android/device/{deviceId}/lastpumpcleaning",
                userDao, deviceDao
            )

            // get
            getAllKwiatonomousDeviceUpdates(
                "/kwiatonomous/android/device/{deviceId}/updates",
                userDao, deviceUpdateDao
            )

            // get
            getKwiatonomousDeviceConfiguration(
                "/kwiatonomous/android/device/{deviceId}/configuration",
                userDao, deviceConfigurationDao
            )

            // post
            updateKwiatonomousDeviceConfiguration(
                "/kwiatonomous/android/device/{deviceId}/configuration",
                userDao, deviceConfigurationDao
            )

            // get
            getAllKwiatonomousDeviceEvents(
                "/kwiatonomous/android/device/{deviceId}/events",
                userDao, deviceEventDao
            )

            // post
            addKwiatonomousDeviceEvent(
                "/kwiatonomous/android/device/{deviceId}/events",
                userDao, deviceEventDao
            )

            // delete
            removeKwiatonomousDeviceEvent(
                "/kwiatonomous/android/device/{deviceId}/events",
                userDao, deviceEventDao
            )
        }

        // ---------------------------------------- For esp8266 ---------------------------------------- //
        // post
        addKwiatonomousDeviceUpdate("/kwiatonomous/esp/{id}/updates", deviceDao, deviceUpdateDao)

        // get
        getKwiatonomousDeviceNextWateringESP("/kwiatonomous/esp/{id}/nextwatering", deviceDao)

        // post
        updateKwiatonomousDeviceNextWateringESP("/kwiatonomous/esp/{id}/nextwatering", deviceDao)

        // get
        getKwiatonomousDeviceConfigurationESP("/kwiatonomous/esp/{id}/configuration", deviceConfigurationDao)

        // post
        addKwiatonomousDeviceEventESP("/kwiatonomous/esp/{id}/events", deviceDao, deviceEventDao)
    }
}
