package com.corrot.kwiatonomousapp.presentation.devices

import com.corrot.kwiatonomousapp.MainCoroutineRule
import com.corrot.kwiatonomousapp.domain.model.User
import com.corrot.kwiatonomousapp.domain.model.UserDevice
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
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

    private var userRepository: UserRepository = mockk()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_some_devices_found() = coroutineRule.runBlockingTest {
        // GIVEN
        val userDevicesList = listOf<UserDevice>(mockk(), mockk(), mockk())
        val user = User("testid", userDevicesList, mockk(), mockk(), true)
        every { userRepository.getCurrentUserFromDatabase() }.returns(flowOf(user))
        val devicesViewModel = DevicesViewModel(userRepository, coroutineRule.dispatcher)

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
        val user = User("testid", emptyList(), mockk(), mockk(), true)
        every { userRepository.getCurrentUserFromDatabase() }.returns(flowOf(user))
        val devicesViewModel = DevicesViewModel(userRepository, coroutineRule.dispatcher)

        // THEN
        val state = devicesViewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.userDevices).isEmpty()
    }
}