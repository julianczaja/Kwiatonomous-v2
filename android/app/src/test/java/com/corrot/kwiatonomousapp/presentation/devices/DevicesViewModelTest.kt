package com.corrot.kwiatonomousapp.presentation.devices

import com.corrot.kwiatonomousapp.MainCoroutineRule
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import com.corrot.kwiatonomousapp.domain.usecase.GetDevicesUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class DevicesViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private var deviceRepository: DeviceRepository = mockk()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_some_devices_found() = coroutineRule.runBlockingTest {
        // GIVEN
        val devicesList = listOf<Device>(mockk(), mockk(), mockk())
        every { deviceRepository.getDevicesFromDatabase() }.returns(flowOf(devicesList))
        coEvery { deviceRepository.fetchDevices() }.returns(emptyList())
        coEvery { deviceRepository.saveFetchedDevices(any()) }.returns(Unit)
        val getDevicesUseCase = GetDevicesUseCase(deviceRepository)
        val devicesViewModel = DevicesViewModel(getDevicesUseCase, coroutineRule.dispatcher)

        // THEN
        val state = devicesViewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.devices).hasSize(3)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_no_devices_found() = coroutineRule.runBlockingTest {
        // GIVEN
        every { deviceRepository.getDevicesFromDatabase() }.returns(flowOf(emptyList()))
        coEvery { deviceRepository.fetchDevices() }.returns(emptyList())
        coEvery { deviceRepository.saveFetchedDevices(any()) }.returns(Unit)
        val getDevicesUseCase = GetDevicesUseCase(deviceRepository)
        val devicesViewModel = DevicesViewModel(getDevicesUseCase, coroutineRule.dispatcher)

        // THEN
        val state = devicesViewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.devices).isEmpty()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_error() = coroutineRule.runBlockingTest {
        // GIVEN
        every { deviceRepository.getDevicesFromDatabase() }.returns(flowOf(emptyList()))
        coEvery { deviceRepository.fetchDevices() }.throws(Exception("No internet connection!"))
        val getDevicesUseCase = GetDevicesUseCase(deviceRepository)
        val devicesViewModel = DevicesViewModel(getDevicesUseCase, coroutineRule.dispatcher)

        // THEN
        val state = devicesViewModel.state.value

        println(state)
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isEqualTo("No internet connection!")
        assertThat(state.devices).isEmpty()
    }
}