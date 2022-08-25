package com.corrot.kwiatonomousapp.presentation.devices

import com.corrot.kwiatonomousapp.MainCoroutineRule
import com.corrot.kwiatonomousapp.domain.usecase.GetUserDevicesWithLastUpdatesUseCase
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

    private val getUserDevicesWithLastUpdatesUseCase: GetUserDevicesWithLastUpdatesUseCase = mockk()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_some_devices_found() = coroutineRule.runBlockingTest {
        // GIVEN
        every { getUserDevicesWithLastUpdatesUseCase.execute() }.returns(
            flowOf(
                listOf(
                    Pair(mockk(), mockk()),
                    Pair(mockk(), mockk()),
                    Pair(mockk(), mockk()),
                )
            )
        )
        val devicesViewModel = DevicesViewModel(
            getUserDevicesWithLastUpdatesUseCase, coroutineRule.dispatcher
        )

        // THEN
        val state = devicesViewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.userDevicesWithLastUpdates).hasSize(3)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_no_devices_found() = coroutineRule.runBlockingTest {
        // GIVEN
        every { getUserDevicesWithLastUpdatesUseCase.execute() }.returns(flowOf(emptyList()))
        val devicesViewModel = DevicesViewModel(
            getUserDevicesWithLastUpdatesUseCase, coroutineRule.dispatcher
        )

        // THEN
        val state = devicesViewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.userDevicesWithLastUpdates).isEmpty()
    }
}