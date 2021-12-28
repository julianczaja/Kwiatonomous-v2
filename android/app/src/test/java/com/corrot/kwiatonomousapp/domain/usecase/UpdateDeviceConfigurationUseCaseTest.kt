package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.data.repository.FakeDeviceConfigurationRepository
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.repository.DeviceConfigurationRepository
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class UpdateDeviceConfigurationUseCaseTest {

    private lateinit var fakeDeviceConfigurationRepository: DeviceConfigurationRepository
    private lateinit var fakeUpdateDeviceConfigurationUseCase: UpdateDeviceConfigurationUseCase

    @Before
    fun setUp() {
        fakeDeviceConfigurationRepository = FakeDeviceConfigurationRepository()
        fakeUpdateDeviceConfigurationUseCase =
            UpdateDeviceConfigurationUseCase(fakeDeviceConfigurationRepository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun execute_failure_blank_id() {
        // GIVEN
        val deviceConfigurationMock = mockk<DeviceConfiguration>()

        // THEN
        Assert.assertThrows(Exception::class.java) {
            runBlockingTest {
                fakeUpdateDeviceConfigurationUseCase.execute("", deviceConfigurationMock)
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun execute_failure_unknown_id() {
        // GIVEN
        val deviceConfigurationMock = mockk<DeviceConfiguration>()

        // THEN
        Assert.assertThrows(Exception::class.java) {
            runBlockingTest {
                fakeUpdateDeviceConfigurationUseCase.execute("aaaa", deviceConfigurationMock)
            }
        }
    }
}