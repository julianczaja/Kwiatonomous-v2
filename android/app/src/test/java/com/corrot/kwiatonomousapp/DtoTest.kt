package com.corrot.kwiatonomousapp

import com.corrot.kwiatonomousapp.data.remote.dto.DeviceConfigurationDto
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceDto
import com.corrot.kwiatonomousapp.data.remote.dto.DeviceUpdateDto
import com.corrot.kwiatonomousapp.data.remote.dto.toDevice
import com.corrot.kwiatonomousapp.data.remote.dto.toDeviceConfiguration
import com.corrot.kwiatonomousapp.data.remote.dto.toDeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.model.toDeviceConfigurationDto
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class DtoTest {

    @Test
    fun deviceConfigurationDtoToDeviceConfiguration() {
        // GIVEN
        val deviceConfigurationDto = DeviceConfigurationDto(
            sleepTimeMinutes = 30,
            timeZoneOffset = 1,
            wateringOn = 1,
            wateringIntervalDays = 2,
            wateringAmount = 250,
            wateringTime = "15:05"
        )

        // WHEN
        val result = deviceConfigurationDto.toDeviceConfiguration("test_id")

        // THEN
        assertThat(result).isEqualTo(
            DeviceConfiguration(
                deviceId = "test_id",
                sleepTimeMinutes = 30,
                timeZoneOffset = ZoneOffset.ofHours(1),
                wateringOn = true,
                wateringIntervalDays = 2,
                wateringAmount = 250,
                wateringTime = LocalTime.of(15, 5)
            )
        )
    }

    @Test
    fun deviceConfigurationToDeviceConfigurationDto() {
        // GIVEN
        val deviceConfiguration = DeviceConfiguration(
            deviceId = "test_id",
            sleepTimeMinutes = 30,
            timeZoneOffset = ZoneOffset.ofHours(1),
            wateringOn = true,
            wateringIntervalDays = 2,
            wateringAmount = 250,
            wateringTime = LocalTime.of(15, 5)
        )

        // WHEN
        val result = deviceConfiguration.toDeviceConfigurationDto()

        // THEN
        assertThat(result).isEqualTo(
            DeviceConfigurationDto(
                sleepTimeMinutes = 30,
                timeZoneOffset = 1,
                wateringOn = 1,
                wateringIntervalDays = 2,
                wateringAmount = 250,
                wateringTime = "15:05"
            )
        )
    }

    @Test
    fun deviceDtoToDevice() {
        // GIVEN
        val deviceDto = DeviceDto(
            deviceId = "id",
            birthday = 1676029938,
            nextWatering = 1676029939,
            lastPumpCleaning = 1676029940,
            lastUpdate = 1676029941
        )

        // WHEN
        val result = deviceDto.toDevice()

        // THEN
        assertThat(result).isEqualTo(
            Device(
                deviceId = "id",
                birthday = LocalDateTime.of(2023, 2, 10, 11, 52, 18),
                nextWatering = LocalDateTime.of(2023, 2, 10, 11, 52, 19),
                lastPumpCleaning = LocalDateTime.of(2023, 2, 10, 11, 52, 20),
                lastUpdate = LocalDateTime.of(2023, 2, 10, 11, 52, 21),
            )
        )
    }

    @Test
    fun deviceUpdateDtoToDeviceUpdate() {
        // GIVEN
        val deviceUpdateDto = DeviceUpdateDto(
            updateId = 5,
            deviceId = "id",
            timestamp = 1639751250L,
            batteryLevel = 55,
            batteryVoltage = 3.65f,
            temperature = 23.5f,
            humidity = 64.5f
        )

        // WHEN
        val result = deviceUpdateDto.toDeviceUpdate()

        // THEN
        assertThat(result).isEqualTo(
            DeviceUpdate(
                deviceId = "id",
                updateTime = LocalDateTime.of(2021, 12, 17, 14, 27, 30),
                batteryLevel = 55,
                batteryVoltage = 3.65f,
                temperature = 23.5f,
                humidity = 64.5f
            )
        )
    }
}