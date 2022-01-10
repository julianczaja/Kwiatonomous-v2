package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.data.repository.FakeDeviceConfigurationRepository
import com.corrot.kwiatonomousapp.domain.model.DeviceConfiguration
import com.corrot.kwiatonomousapp.domain.repository.DeviceConfigurationRepository
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import java.time.LocalTime
import java.time.ZoneOffset

class GetDeviceConfigurationUseCaseTest {

    private lateinit var fakeDeviceConfigurationRepository: DeviceConfigurationRepository
    private lateinit var fakeGetDeviceConfigurationUseCase: GetDeviceConfigurationUseCase

    @Before
    fun setUp() {
        fakeDeviceConfigurationRepository = FakeDeviceConfigurationRepository()
        fakeGetDeviceConfigurationUseCase =
            GetDeviceConfigurationUseCase(fakeDeviceConfigurationRepository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun execute_success() = runBlockingTest {
        // GIVEN
        val flow = fakeGetDeviceConfigurationUseCase.execute("id1")
        val collected = flow.toList()

        // WHEN
        val correctResult =
            DeviceConfiguration(30, ZoneOffset.ofHours(1), true, 2, 100, LocalTime.of(10, 0))

        // THEN
        Truth.assertThat(collected.size).isEqualTo(2)
        Truth.assertThat(collected[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(collected[1]).isInstanceOf(Result.Success::class.java)
        val data = (collected[1] as Result.Success<DeviceConfiguration>).data
        Truth.assertThat(data).isEqualTo(correctResult)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun execute_failure() = runBlockingTest {
        // GIVEN
        val flow = fakeGetDeviceConfigurationUseCase.execute("wrong_id")
        val collected = flow.toList()

        // THEN
        Truth.assertThat(collected.size).isEqualTo(2)
        Truth.assertThat(collected[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(collected[1]).isInstanceOf(Result.Error::class.java)
        val error = (collected[1] as Result.Error).throwable
        Truth.assertThat(error).isInstanceOf(Exception::class.java)
    }
}