package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.common.toLocalDateTime
import com.corrot.kwiatonomousapp.data.repository.FakeDeviceUpdateRepository
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetDeviceUpdatesByDateUseCaseTest {

    private lateinit var fakeDeviceUpdateRepository: DeviceUpdateRepository
    private lateinit var fakeGetDeviceUpdatesByDateUseCase: GetDeviceUpdatesByDateUseCase

    @Before
    fun setUp() {
        fakeDeviceUpdateRepository = FakeDeviceUpdateRepository()
        fakeGetDeviceUpdatesByDateUseCase =
            GetDeviceUpdatesByDateUseCase(fakeDeviceUpdateRepository)
    }

    @Test
    fun execute_success() = runTest {
        // GIVEN
        val correctSize = 2
        val correctDeviceUpdate =
            DeviceUpdate("id1", 1200000000L.toLocalDateTime(), 80, 3.75f, 24.5f, 53.5f)

        // WHEN
        val devices = fakeGetDeviceUpdatesByDateUseCase.execute("id1", 1100000000L, 1200000000L)
        val collected = devices.toList()

        // THEN
        assertThat(collected.size).isEqualTo(2)
        assertThat(collected[0]).isInstanceOf(Result.Loading::class.java)
        assertThat(collected[1]).isInstanceOf(Result.Success::class.java)
        val data = (collected[1] as Result.Success<List<DeviceUpdate>>).data
        assertThat(data.size).isEqualTo(correctSize)
        assertThat(data.last()).isEqualTo(correctDeviceUpdate)
    }

    @Test
    fun execute_failure() = runTest {
        // GIVEN
        val flow = fakeGetDeviceUpdatesByDateUseCase.execute("wrong_id", 1100000000L, 1200000000L)
        val collected = flow.toList()

        // THEN
        assertThat(collected.size).isEqualTo(2)
        assertThat(collected[0]).isInstanceOf(Result.Loading::class.java)
        assertThat(collected[1]).isInstanceOf(Result.Error::class.java)
        val error = (collected[1] as Result.Error).throwable
        assertThat(error).isInstanceOf(Exception::class.java)
    }
}