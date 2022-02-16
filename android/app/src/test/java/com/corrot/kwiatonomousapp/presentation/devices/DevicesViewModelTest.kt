package com.corrot.kwiatonomousapp.presentation.devices

import com.corrot.kwiatonomousapp.MainCoroutineRule
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.repository.UserDeviceRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
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

    private var userDeviceRepository: UserDeviceRepository = mockk()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_some_devices_found() = coroutineRule.runBlockingTest {
        // GIVEN
        val userDevicesList = listOf<UserDevice>(mockk(), mockk(), mockk())
        every { userDeviceRepository.getUserDevices() }.returns(flowOf(userDevicesList))
        val devicesViewModel = DevicesViewModel(userDeviceRepository, coroutineRule.dispatcher)

        // THEN
        val state = devicesViewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.userDevices).hasSize(3)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_no_devices_found() = coroutineRule.runBlockingTest {
        // GIVEN
        every { userDeviceRepository.getUserDevices() }.returns(flowOf(emptyList()))
        val devicesViewModel = DevicesViewModel(userDeviceRepository, coroutineRule.dispatcher)

        // THEN
        val state = devicesViewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.userDevices).isEmpty()
    }
}