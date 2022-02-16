package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.data.repository.FakeDeviceRepository
import com.corrot.kwiatonomousapp.data.repository.FakeUserDeviceRepository
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import com.corrot.kwiatonomousapp.domain.repository.UserDeviceRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test


class CheckIfDeviceExistsUseCaseTest {

    private lateinit var fakeDeviceRepository: DeviceRepository
    private lateinit var fakeUserDeviceRepository: UserDeviceRepository
    private lateinit var fakeCheckIfDeviceExistsUseCase: CheckIfDeviceExistsUseCase

    @Before
    fun setUp() {
        fakeDeviceRepository = FakeDeviceRepository()
        fakeUserDeviceRepository = FakeUserDeviceRepository()
        fakeCheckIfDeviceExistsUseCase = CheckIfDeviceExistsUseCase(
            deviceRepository = fakeDeviceRepository,
            userDeviceRepository = fakeUserDeviceRepository
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_deviceId_found_remote_and_not_found_local() = runBlockingTest {
        // GIVEN
        val deviceId = "id3"

        // WHEN
        val flow = fakeCheckIfDeviceExistsUseCase.execute(deviceId)
        val collected = flow.toList()

        // THEN
        assertThat(collected.size).isEqualTo(2)
        assertThat(collected[0]).isInstanceOf(Result.Loading::class.java)
        assertThat(collected[1]).isInstanceOf(Result.Success::class.java)
        val data = (collected[1] as Result.Success<Boolean>).data
        assertThat(data).isEqualTo(true)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_deviceId_not_found_remote() = runBlockingTest {
        // GIVEN
        val deviceId = "unknownId"

        // WHEN
        val flow = fakeCheckIfDeviceExistsUseCase.execute(deviceId)
        val collected = flow.toList()

        // THEN
        assertThat(collected.size).isEqualTo(2)
        assertThat(collected[0]).isInstanceOf(Result.Loading::class.java)
        assertThat(collected[1]).isInstanceOf(Result.Success::class.java)
        val data = (collected[1] as Result.Success<Boolean>).data
        assertThat(data).isEqualTo(false)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun test_deviceId_found_remote_and_found_local() = runBlockingTest {
        // GIVEN
        val deviceId = "id1"

        // WHEN
        val flow = fakeCheckIfDeviceExistsUseCase.execute(deviceId)
        val collected = flow.toList()

        // THEN
        assertThat(collected.size).isEqualTo(2)
        assertThat(collected[0]).isInstanceOf(Result.Loading::class.java)
        assertThat(collected[1]).isInstanceOf(Result.Error::class.java)
        val throwable = (collected[1] as Result.Error).throwable
        assertThat(throwable.message).isEqualTo("Device with ID \"id1\" is already added")
    }
}