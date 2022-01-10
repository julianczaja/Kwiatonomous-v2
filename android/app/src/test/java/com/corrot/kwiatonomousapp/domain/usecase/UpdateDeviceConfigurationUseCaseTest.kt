package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.data.repository.FakeDeviceConfigurationRepository
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.repository.DeviceConfigurationRepository
import com.google.common.truth.Truth
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
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
    fun execute_failure_blank_id() = runBlockingTest {
        // GIVEN
        val deviceConfigurationMock = mockk<DeviceConfiguration>()

        // WHEN
        val flow = fakeUpdateDeviceConfigurationUseCase.execute("", deviceConfigurationMock)
        val collected = flow.toList()

        // THEN
        Truth.assertThat(collected.size).isEqualTo(2)
        Truth.assertThat(collected[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(collected[1]).isInstanceOf(Result.Error::class.java)
        val error = (collected[1] as Result.Error).throwable
        Truth.assertThat(error).isInstanceOf(Exception::class.java)

//        // THEN
//        Assert.assertThrows(Exception::class.java) {
//            runBlockingTest {
//                fakeUpdateDeviceConfigurationUseCase.execute("", deviceConfigurationMock)
//            }
//        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun execute_failure_unknown_id() = runBlockingTest {
        // GIVEN
        val deviceConfigurationMock = mockk<DeviceConfiguration>()

        // WHEN
        val flow = fakeUpdateDeviceConfigurationUseCase.execute("aaaa", deviceConfigurationMock)
        val collected = flow.toList()

        // THEN
        Truth.assertThat(collected.size).isEqualTo(2)
        Truth.assertThat(collected[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(collected[1]).isInstanceOf(Result.Error::class.java)
        val error = (collected[1] as Result.Error).throwable
        Truth.assertThat(error).isInstanceOf(Exception::class.java)

//        // THEN
//        Assert.assertThrows(Exception::class.java) {
//            runBlockingTest {
//                fakeUpdateDeviceConfigurationUseCase.execute("aaaa", deviceConfigurationMock)
//            }
//        }
    }
}