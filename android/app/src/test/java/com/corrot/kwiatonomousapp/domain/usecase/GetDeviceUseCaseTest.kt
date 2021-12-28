package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.data.repository.FakeDeviceRepository
import com.corrot.kwiatonomousapp.domain.model.Device
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class GetDeviceUseCaseTest {

    private lateinit var fakeDeviceRepository: DeviceRepository
    private lateinit var fakeGetDeviceUseCase: GetDeviceUseCase

    @Before
    fun setUp() {
        fakeDeviceRepository = FakeDeviceRepository()
        fakeGetDeviceUseCase = GetDeviceUseCase(fakeDeviceRepository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun execute_success() = runBlockingTest {
        // GIVEN
        val flow = fakeGetDeviceUseCase.execute("id2")
        val collected = flow.toList()

        // WHEN
        val correctId = "id2"
        val correctBirthday = LocalDateTime.of(2021, 12, 22, 12, 0, 52)
        val correctLastUpdate = LocalDateTime.of(2021, 12, 22, 12, 0, 52)
        val correctNextWatering = LocalDateTime.of(2021, 12, 22, 12, 0, 52)

        // THEN
        assertThat(collected.size).isEqualTo(2)
        assertThat(collected[0]).isInstanceOf(Result.Loading::class.java)
        assertThat(collected[1]).isInstanceOf(Result.Success::class.java)
        val data = (collected[1] as Result.Success<Device>).data
        assertThat(data.id).isEqualTo(correctId)
        assertThat(data.birthday).isEqualTo(correctBirthday)
        assertThat(data.lastUpdate).isEqualTo(correctLastUpdate)
        assertThat(data.nextWatering).isEqualTo(correctNextWatering)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun execute_failure() = runBlockingTest {
        // GIVEN
        val flow = fakeGetDeviceUseCase.execute("wrong_id")
        val collected = flow.toList()

        // THEN
        assertThat(collected.size).isEqualTo(2)
        assertThat(collected[0]).isInstanceOf(Result.Loading::class.java)
        assertThat(collected[1]).isInstanceOf(Result.Error::class.java)
        val error = (collected[1] as Result.Error).throwable
        assertThat(error).isInstanceOf(Exception::class.java)
    }
}