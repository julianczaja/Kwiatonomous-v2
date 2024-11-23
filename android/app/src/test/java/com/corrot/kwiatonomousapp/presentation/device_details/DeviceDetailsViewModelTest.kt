package com.corrot.kwiatonomousapp.presentation.device_details

import androidx.lifecycle.SavedStateHandle
import com.corrot.kwiatonomousapp.MainDispatcherRule
import com.corrot.kwiatonomousapp.common.Constants.NAV_ARG_DEVICE_ID
import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.domain.model.AppTheme
import com.corrot.kwiatonomousapp.domain.model.ChartSettings
import com.corrot.kwiatonomousapp.domain.model.DeviceEvent
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.repository.AppPreferencesRepository
import com.corrot.kwiatonomousapp.domain.repository.DeviceConfigurationRepository
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import com.corrot.kwiatonomousapp.domain.repository.UserRepository
import com.corrot.kwiatonomousapp.domain.usecase.AddDeviceEventUseCase
import com.corrot.kwiatonomousapp.domain.usecase.DeleteDeviceEventUseCase
import com.corrot.kwiatonomousapp.domain.usecase.DeleteUserDeviceUseCase
import com.corrot.kwiatonomousapp.domain.usecase.GetAllDeviceEventsUseCase
import com.corrot.kwiatonomousapp.domain.usecase.GetDeviceConfigurationUseCase
import com.corrot.kwiatonomousapp.domain.usecase.GetDeviceUpdatesByDateUseCase
import com.corrot.kwiatonomousapp.domain.usecase.GetDeviceUseCase
import com.corrot.kwiatonomousapp.domain.usecase.GetUserDeviceUseCase
import com.corrot.kwiatonomousapp.domain.usecase.UpdateDeviceLastPumpCleaningUseCase
import com.corrot.kwiatonomousapp.domain.usecase.UpdateUserDeviceUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class DeviceDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

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
    private val updateDeviceLastPumpCleaningUseCase: UpdateDeviceLastPumpCleaningUseCase = mockk()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { appPreferencesRepository.getAppTheme() }.returns(flowOf(AppTheme.DARK))
        every { appPreferencesRepository.getChartSettings() }.returns(flowOf(ChartSettings()))
    }

    @Test
    fun test_device_found() = runTest {
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

        // Mock updateDeviceLastPumpCleaningUseCase
        coEvery { updateDeviceLastPumpCleaningUseCase.execute(any(), any()) }
            .returns(flowOf(Result.Success(null)))

        val deviceDetailsViewModel = DeviceDetailsViewModel(
            savedStateHandle = SavedStateHandle().apply { set(NAV_ARG_DEVICE_ID, deviceId) },
            appPreferencesRepository = appPreferencesRepository,
            getDeviceUseCase = getDeviceUseCase,
            getDeviceUpdatesByDateUseCase = getDeviceUpdatesByDateUseCase,
            getDeviceConfigurationUseCase = getDeviceConfigurationUseCase,
            getUserDeviceUseCase = getUserDeviceUseCase,
            deleteUserDeviceUseCase = deleteUserDeviceUseCase,
            ioDispatcher = mainDispatcherRule.testDispatcher,
            userRepository = userRepository,
            updateUserDeviceUseCase = updateUserDeviceUseCase,
            getAllDeviceEventsUseCase = getAllDeviceEventsUseCase,
            addDeviceEventUseCase = addDeviceEventUseCase,
            deleteDeviceEventUseCase = deleteDeviceEventUseCase,
            updateDeviceLastPumpCleaningUseCase = updateDeviceLastPumpCleaningUseCase
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

    @Test
    fun test_error() = runTest {
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

        // Mock updateDeviceLastPumpCleaningUseCase
        coEvery { updateDeviceLastPumpCleaningUseCase.execute(any(), any()) }
            .returns(flowOf(Result.Success(null)))

        val deviceDetailsViewModel = DeviceDetailsViewModel(
            savedStateHandle = SavedStateHandle().apply { set(NAV_ARG_DEVICE_ID, deviceId) },
            appPreferencesRepository = appPreferencesRepository,
            getDeviceUseCase = getDeviceUseCase,
            getDeviceUpdatesByDateUseCase = getDeviceUpdatesByDateUseCase,
            getDeviceConfigurationUseCase = getDeviceConfigurationUseCase,
            getUserDeviceUseCase = getUserDeviceUseCase,
            deleteUserDeviceUseCase = deleteUserDeviceUseCase,
            ioDispatcher = mainDispatcherRule.testDispatcher,
            userRepository = userRepository,
            updateUserDeviceUseCase = updateUserDeviceUseCase,
            getAllDeviceEventsUseCase = getAllDeviceEventsUseCase,
            addDeviceEventUseCase = addDeviceEventUseCase,
            deleteDeviceEventUseCase = deleteDeviceEventUseCase,
            updateDeviceLastPumpCleaningUseCase = updateDeviceLastPumpCleaningUseCase
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