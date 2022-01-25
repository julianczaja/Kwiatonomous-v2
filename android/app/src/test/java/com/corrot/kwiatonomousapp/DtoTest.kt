package com.corrot.kwiatonomousapp

import com.corrot.kwiatonomousapp.data.remote.dto.*
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
            "id",
            1639751250L,
            1639837650L,
            1639123456L
        )

        // WHEN
        val result = deviceDto.toDevice()

        // THEN
        assertThat(result).isEqualTo(
            Device(
                deviceId = "id",
                birthday = LocalDateTime.of(2021, 12, 17, 14, 27, 30),
                lastUpdate = LocalDateTime.of(2021, 12, 18, 14, 27, 30),
                nextWatering = LocalDateTime.of(2021, 12, 10, 8, 4, 16)
            )
        )
    }

    @Test
    fun deviceUpdateDtoToDeviceUpdate() {
        // GIVEN
        val deviceUpdateDto = DeviceUpdateDto(
            5,
            "id",
            1639751250L,
            55,
            3.65f,
            23.5f,
            64.5f
        )

        // WHEN
        val result = deviceUpdateDto.toDeviceUpdate()

        // THEN
        assertThat(result).isEqualTo(
            DeviceUpdate(
                "id",
                LocalDateTime.of(2021, 12, 17, 14, 27, 30),
                55,
                3.65f,
                23.5f,
                64.5f
            )
        )
    }
}