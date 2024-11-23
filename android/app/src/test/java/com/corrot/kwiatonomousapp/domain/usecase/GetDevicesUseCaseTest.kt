package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.data.repository.FakeDeviceRepository
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import com.google.common.truth.Truth
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class GetDevicesUseCaseTest {

    private lateinit var fakeDeviceRepository: DeviceRepository
    private lateinit var fakeGetDevicesUseCase: GetDevicesUseCase

    @Before
    fun setUp() {
        fakeDeviceRepository = FakeDeviceRepository()
        fakeGetDevicesUseCase = GetDevicesUseCase(fakeDeviceRepository)
    }

    @Test
    fun execute_success() = runTest {
        // GIVEN
        val devices = fakeGetDevicesUseCase.execute()
        val collected = devices.toList()

        // WHEN
        val correctSize = 3
        val correctBirthday = LocalDateTime.of(2021, 12, 22, 12, 0, 53)

        // THEN
        Truth.assertThat(collected.size).isEqualTo(2)
        Truth.assertThat(collected[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(collected[1]).isInstanceOf(Result.Success::class.java)
        val data = (collected[1] as Result.Success<List<Device>>).data
        Truth.assertThat(data.size).isEqualTo(correctSize)
        Truth.assertThat(data.last().birthday).isEqualTo(correctBirthday)
    }
}