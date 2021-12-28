package com.corrot.kwiatonomousapp.domain.usecase

import com.corrot.kwiatonomousapp.common.Result
import com.corrot.kwiatonomousapp.data.repository.FakeDeviceRepository
import com.corrot.kwiatonomousapp.domain.repository.DeviceRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class GetDeviceNextWateringUseCaseTest {

    private lateinit var fakeDeviceRepository: DeviceRepository
    private lateinit var fakeGetDeviceNextWateringUseCase: GetDeviceNextWateringUseCase

    @Before
    fun setUp() {
        fakeDeviceRepository = FakeDeviceRepository()
        fakeGetDeviceNextWateringUseCase = GetDeviceNextWateringUseCase(fakeDeviceRepository)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun execute_success() = runBlockingTest {
        // GIVEN
        val flow = fakeGetDeviceNextWateringUseCase.execute("id2")
        val collected = flow.toList()

        // WHEN
        val correctResult = LocalDateTime.of(2021, 12, 22, 12, 0, 52)

        // THEN
        assertThat(collected.size).isEqualTo(2)
        assertThat(collected[0]).isInstanceOf(Result.Loading::class.java)
        assertThat(collected[1]).isInstanceOf(Result.Success::class.java)
        val data = (collected[1] as Result.Success<LocalDateTime>).data
        assertThat(data).isEqualTo(correctResult)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun execute_failure() = runBlockingTest {
        // GIVEN
        val flow = fakeGetDeviceNextWateringUseCase.execute("wrong_id")
        val collected = flow.toList()

        // THEN
        assertThat(collected.size).isEqualTo(2)
        assertThat(collected[0]).isInstanceOf(Result.Loading::class.java)
        assertThat(collected[1]).isInstanceOf(Result.Error::class.java)
        val error = (collected[1] as Result.Error).throwable
        assertThat(error).isInstanceOf(Exception::class.java)
    }
}