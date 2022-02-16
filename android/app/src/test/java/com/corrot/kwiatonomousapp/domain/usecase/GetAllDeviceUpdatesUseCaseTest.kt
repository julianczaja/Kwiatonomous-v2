package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.common.toLocalDateTime
import com.corrot.kwiatonomousapp.data.repository.FakeDeviceUpdateRepository
import com.corrot.kwiatonomousapp.domain.model.DeviceUpdate
import com.corrot.kwiatonomousapp.domain.repository.DeviceUpdateRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

class GetAllDeviceUpdatesUseCaseTest {

    private lateinit var fakeDeviceUpdateRepository: DeviceUpdateRepository
    private lateinit var fakeGetAllDeviceUpdatesUseCase: GetAllDeviceUpdatesUseCase

    @Before
    fun setUp() {
        fakeDeviceUpdateRepository = FakeDeviceUpdateRepository()
        fakeGetAllDeviceUpdatesUseCase = GetAllDeviceUpdatesUseCase(fakeDeviceUpdateRepository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_found_multiple() = runBlockingTest {
        // GIVEN
        val correctSize = 3
        val correctDeviceUpdate =
            DeviceUpdate("id1", 1200000000L.toLocalDateTime(), 80, 3.75f, 24.5f, 53.5f)

        // WHEN
        val devices = fakeGetAllDeviceUpdatesUseCase.execute("id1", correctSize)
        val collected = devices.toList()

        // THEN
        assertThat(collected.size).isEqualTo(2)
        assertThat(collected[0]).isInstanceOf(Result.Loading::class.java)
        assertThat(collected[1]).isInstanceOf(Result.Success::class.java)
        val data = (collected[1] as Result.Success<List<DeviceUpdate>>).data
        assertThat(data.size).isEqualTo(correctSize)
        assertThat(data.last()).isEqualTo(correctDeviceUpdate)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_found_empty() = runBlockingTest {
        // GIVEN
        val flow = fakeGetAllDeviceUpdatesUseCase.execute("id2", 1)
        val collected = flow.toList()

        // THEN
        assertThat(collected.size).isEqualTo(2)
        assertThat(collected[0]).isInstanceOf(Result.Loading::class.java)
        assertThat(collected[1]).isInstanceOf(Result.Success::class.java)
        val data = (collected[1] as Result.Success).data
        assertThat(data).isEmpty()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_error() = runBlockingTest {
        // GIVEN
        val flow = fakeGetAllDeviceUpdatesUseCase.execute("wrong_id", 5)
        val collected = flow.toList()

        // THEN
        assertThat(collected.size).isEqualTo(2)
        assertThat(collected[0]).isInstanceOf(Result.Loading::class.java)
        assertThat(collected[1]).isInstanceOf(Result.Error::class.java)
        val error = (collected[1] as Result.Error).throwable
        assertThat(error).isInstanceOf(Exception::class.java)
    }
}