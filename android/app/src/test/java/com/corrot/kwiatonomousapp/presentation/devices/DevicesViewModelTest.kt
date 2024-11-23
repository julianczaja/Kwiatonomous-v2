package com.corrot.kwiatonomousapp.presentation.devices

import com.corrot.kwiatonomousapp.MainDispatcherRule
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.usecase.GetUserDevicesWithLastUpdatesUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class DevicesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getUserDevicesWithLastUpdatesUseCase: GetUserDevicesWithLastUpdatesUseCase = mockk()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun test_some_devices_found() = runTest {
        // GIVEN
        every { getUserDevicesWithLastUpdatesUseCase.execute() }.returns(
            flowOf(
                Result.Success(
                    listOf(
                        Pair(mockk(), mockk()),
                        Pair(mockk(), mockk()),
                        Pair(mockk(), mockk()),
                    )
                )
            )
        )
        val devicesViewModel = DevicesViewModel(
            getUserDevicesWithLastUpdatesUseCase, mainDispatcherRule.testDispatcher
        )

        // THEN
        val state = devicesViewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.userDevicesWithLastUpdates).hasSize(3)
    }

    @Test
    fun test_no_devices_found() = runTest {
        // GIVEN
        every { getUserDevicesWithLastUpdatesUseCase.execute() }.returns(
            flowOf(Result.Success(emptyList()))
        )
        val devicesViewModel = DevicesViewModel(
            getUserDevicesWithLastUpdatesUseCase, mainDispatcherRule.testDispatcher
        )

        // THEN
        val state = devicesViewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.userDevicesWithLastUpdates).isEmpty()
    }
}