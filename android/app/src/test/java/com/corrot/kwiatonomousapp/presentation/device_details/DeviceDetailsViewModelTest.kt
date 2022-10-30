package com.corrot.kwiatonomousapp.presentation.device_details

import androidx.lifecycle.SavedStateHandle
import com.corrot.kwiatonomousapp.MainCoroutineRule
import com.corrot.kwiatonomousapp.common.Constants.NAV_ARG_DEVICE_ID
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.AppTheme
import com.corrot.kwiatonomousapp.domain.model.ChartSettings
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.repository.*
import com.corrot.kwiatonomousapp.domain.usecase.*
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class DeviceDetailsViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val appPreferencesRepository: AppPreferencesRepository = mockk()
    private val deviceRepository: DeviceRepository = mockk()
    private val deviceConfigurationRepository: DeviceConfigurationRepository = mockk()
    private val deviceUpdateRepository: DeviceUpdateRepository = mockk()
    private val getUserDeviceUseCase: GetUserDeviceUseCase = mockk()
    private val deleteUserDeviceUseCase: DeleteUserDeviceUseCase = mockk()
    private val userRepository: UserRepository = mockk()
    private val updateUserDeviceUseCase: UpdateUserDeviceUseCase = mockk()
    private val getAllDeviceEventsUseCase: GetAllDeviceEventsUseCase = mockk()
    private val addDeviceEventUseCase: AddDeviceEventUseCase = mockk()
    private val deleteDeviceEventUseCase: DeleteDeviceEventUseCase = mockk()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { appPreferencesRepository.getAppTheme() }.returns(flowOf(AppTheme.DARK))
        every { appPreferencesRepository.getChartSettings() }.returns(flowOf(ChartSettings()))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_device_found() = coroutineRule.runBlockingTest {
        val deviceId = "test_id"

        // Mock GetDeviceUseCase
        every { deviceRepository.getDeviceFromDatabase(deviceId) }.returns(flowOf(mockk()))
        coEvery { deviceRepository.fetchDeviceById(deviceId) }.returns(mockk())
        coEvery { deviceRepository.saveFetchedDevice(any()) }.returns(Unit)
        val getDeviceUseCase = GetDeviceUseCase(deviceRepository)

        // Mock GetDeviceConfigurationUseCase
        every { deviceConfigurationRepository.getDeviceConfigurationFromDatabase(deviceId) }
            .returns(flowOf(mockk()))
        coEvery { deviceConfigurationRepository.fetchDeviceConfigurationByDeviceId(deviceId) }
            .returns(mockk())
        coEvery { deviceConfigurationRepository.saveFetchedDeviceConfiguration(any()) }.returns(Unit)
        val getDeviceConfigurationUseCase =
            GetDeviceConfigurationUseCase(deviceConfigurationRepository)

        // Mock GetDeviceUpdatesByDateUseCase
        val updatesList = listOf<DeviceUpdate>(mockk(), mockk(), mockk())
        every { deviceUpdateRepository.getDeviceUpdatesByDateFromDatabase(deviceId, any(), any()) }
            .returns(flowOf(updatesList))
        coEvery { deviceUpdateRepository.fetchDeviceUpdatesByDate(deviceId, any(), any()) }
            .returns(mockk())
        coEvery { deviceUpdateRepository.saveFetchedDeviceUpdates(any()) }.returns(Unit)
        val getDeviceUpdatesByDateUseCase = GetDeviceUpdatesByDateUseCase(deviceUpdateRepository)

        // Mock GetUserDeviceUseCase
        coEvery { getUserDeviceUseCase.execute(any()) }
            .returns(flowOf(Result.Success(mockk())))

        // Mock UpdateUserDeviceUseCase
        coEvery { updateUserDeviceUseCase.execute(any()) }
            .returns(flowOf(Result.Success(mockk())))

        // Mock getAllDeviceEventsUseCase
        val eventsList = listOf<DeviceEvent>(DeviceEvent.Watering(deviceId, LocalDateTime.now()))
        coEvery { getAllDeviceEventsUseCase.execute(any(), any()) }
            .returns(flowOf(Result.Success(eventsList)))

        // Mock addDeviceEventUseCase
        coEvery { addDeviceEventUseCase.execute(any()) }
            .returns(flowOf(Result.Success(null)))
        // Mock deleteDeviceEventUseCase

        coEvery { deleteDeviceEventUseCase.execute(any()) }
            .returns(flowOf(Result.Success(null)))

        val deviceDetailsViewModel = DeviceDetailsViewModel(
            savedStateHandle = SavedStateHandle().apply { set(NAV_ARG_DEVICE_ID, deviceId) },
            appPreferencesRepository = appPreferencesRepository,
            getDeviceUseCase = getDeviceUseCase,
            getDeviceUpdatesByDateUseCase = getDeviceUpdatesByDateUseCase,
            getDeviceConfigurationUseCase = getDeviceConfigurationUseCase,
            getUserDeviceUseCase = getUserDeviceUseCase,
            deleteUserDeviceUseCase = deleteUserDeviceUseCase,
            ioDispatcher = coroutineRule.dispatcher,
            userRepository = userRepository,
            updateUserDeviceUseCase = updateUserDeviceUseCase,
            getAllDeviceEventsUseCase = getAllDeviceEventsUseCase,
            addDeviceEventUseCase = addDeviceEventUseCase,
            deleteDeviceEventUseCase = deleteDeviceEventUseCase
        )

        println(deviceDetailsViewModel.state.value.error)

        assertThat(deviceDetailsViewModel.isLoading).isFalse()
        assertThat(deviceDetailsViewModel.state.value.userDevice).isNotNull()
        assertThat(deviceDetailsViewModel.state.value.device).isNotNull()
        assertThat(deviceDetailsViewModel.state.value.deviceUpdates).isNotNull()
        assertThat(deviceDetailsViewModel.state.value.deviceUpdates).hasSize(3)
        assertThat(deviceDetailsViewModel.state.value.deviceConfiguration).isNotNull()
        assertThat(deviceDetailsViewModel.state.value.deviceEvents).isNotNull()
//        assertThat(deviceDetailsViewModel.state.value.error).isNull()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_error() = coroutineRule.runBlockingTest {
        val errMsg = "No device found"
        val deviceId = "wrong_id"

        // Mock GetDeviceUseCase
        every { deviceRepository.getDeviceFromDatabase(deviceId) }.returns(emptyFlow())
        coEvery { deviceRepository.fetchDeviceById(deviceId) }.throws(Exception(errMsg))
        val getDeviceUseCase = GetDeviceUseCase(deviceRepository)

        // Mock GetDeviceConfigurationUseCase
        every { deviceConfigurationRepository.getDeviceConfigurationFromDatabase(deviceId) }
            .returns(emptyFlow())
        coEvery { deviceConfigurationRepository.fetchDeviceConfigurationByDeviceId(deviceId) }
            .throws(Exception(errMsg))
        val getDeviceConfigurationUseCase =
            GetDeviceConfigurationUseCase(deviceConfigurationRepository)

        // Mock GetDeviceUpdatesByDateUseCase
        every { deviceUpdateRepository.getDeviceUpdatesByDateFromDatabase(deviceId, any(), any()) }
            .returns(emptyFlow())
        coEvery { deviceUpdateRepository.fetchDeviceUpdatesByDate(deviceId, any(), any()) }
            .throws(Exception(errMsg))
        val getDeviceUpdatesByDateUseCase = GetDeviceUpdatesByDateUseCase(deviceUpdateRepository)

        // Mock GetUserDeviceUseCase
        coEvery { getUserDeviceUseCase.execute(any()) }
            .returns(flowOf(Result.Error(Throwable(errMsg))))

        // Mock UpdateUserDeviceUseCase
        coEvery { updateUserDeviceUseCase.execute(any()) }
            .returns(flowOf(Result.Success(mockk())))

        // Mock getAllDeviceEventsUseCase
        coEvery { getAllDeviceEventsUseCase.execute(any(), any()) }
            .returns(flowOf(Result.Error(Throwable(errMsg))))

        // Mock addDeviceEventUseCase
        coEvery { addDeviceEventUseCase.execute(any()) }
            .returns(flowOf(Result.Success(null)))

        // Mock deleteDeviceEventUseCase
        coEvery { deleteDeviceEventUseCase.execute(any()) }
            .returns(flowOf(Result.Success(null)))

        val deviceDetailsViewModel = DeviceDetailsViewModel(
            savedStateHandle = SavedStateHandle().apply { set(NAV_ARG_DEVICE_ID, deviceId) },
            appPreferencesRepository = appPreferencesRepository,
            getDeviceUseCase = getDeviceUseCase,
            getDeviceUpdatesByDateUseCase = getDeviceUpdatesByDateUseCase,
            getDeviceConfigurationUseCase = getDeviceConfigurationUseCase,
            getUserDeviceUseCase = getUserDeviceUseCase,
            deleteUserDeviceUseCase = deleteUserDeviceUseCase,
            ioDispatcher = coroutineRule.dispatcher,
            userRepository = userRepository,
            updateUserDeviceUseCase = updateUserDeviceUseCase,
            getAllDeviceEventsUseCase = getAllDeviceEventsUseCase,
            addDeviceEventUseCase = addDeviceEventUseCase,
            deleteDeviceEventUseCase = deleteDeviceEventUseCase
        )

        assertThat(deviceDetailsViewModel.isLoading).isFalse()
        assertThat(deviceDetailsViewModel.state.value.userDevice).isNull()
        assertThat(deviceDetailsViewModel.state.value.device).isNull()
        assertThat(deviceDetailsViewModel.state.value.deviceUpdates).isNull()
        assertThat(deviceDetailsViewModel.state.value.deviceConfiguration).isNull()
        assertThat(deviceDetailsViewModel.state.value.deviceEvents).isNull()
        assertThat(deviceDetailsViewModel.state.value.error).isEqualTo(errMsg)

        deviceDetailsViewModel.confirmError()
        assertThat(deviceDetailsViewModel.state.value.error).isNull()
    }
}