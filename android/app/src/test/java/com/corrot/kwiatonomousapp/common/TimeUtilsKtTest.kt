package com.corrot.kwiatonomousapp.common

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime

class TimeUtilsKtTest {

    @Test
    fun longToLocalDateTime() {
        // GIVEN
        val timestamp1 = 1639751250L
        val timestamp2 = 1639837650L
        val timestamp3 = 1639123456L

        // WHEN
        val result1 = timestamp1.toLocalDateTime()
        val result2 = timestamp2.toLocalDateTime()
        val result3 = timestamp3.toLocalDateTime()

        // THEN
        assertThat(result1).isEqualTo(LocalDateTime.of(2021, 12, 17, 14, 27, 30))
        assertThat(result2).isEqualTo(LocalDateTime.of(2021, 12, 18, 14, 27, 30))
        assertThat(result3).isEqualTo(LocalDateTime.of(2021, 12, 10, 8, 4, 16))
    }

    @Test
    fun localDateTimeToFormattedString() {
        // GIVEN
        val localDateTime1 = LocalDateTime.of(2021, 12, 17, 14, 27, 30)
        val localDateTime2 = LocalDateTime.of(2021, 2, 17, 14, 7, 30)
        val localDateTime3 = LocalDateTime.of(2021, 1, 1, 1, 1, 1)

        // WHEN
        val result1 = localDateTime1.toFormattedString()
        val result2 = localDateTime2.toFormattedString()
        val result3 = localDateTime3.toFormattedString()

        // THEN
        assertThat(result1).isEqualTo("17.12.2021, 14:27")
        assertThat(result2).isEqualTo("17.02.2021, 14:07")
        assertThat(result3).isEqualTo("01.01.2021, 01:01")
    }

    @Test
    fun localDateTimeToLong() {
        // GIVEN
        val localDateTime1 = LocalDateTime.of(2021, 12, 17, 14, 27, 30)
        val localDateTime2 = LocalDateTime.of(2021, 12, 18, 14, 27, 30)
        val localDateTime3 = LocalDateTime.of(2021, 12, 10, 8, 4, 16)

        // WHEN
        val result1 = localDateTime1.toLong()
        val result2 = localDateTime2.toLong()
        val result3 = localDateTime3.toLong()

        // THEN
        assertThat(result1).isEqualTo(1639751250L)
        assertThat(result2).isEqualTo(1639837650L)
        assertThat(result3).isEqualTo(1639123456L)
    }

    @Test
    fun localTimeToDatabaseString() {
        // GIVEN
        val localTime1 = LocalTime.of(14, 27, 0)
        val localTime2 = LocalTime.of(1, 27, 30)
        val localTime3 = LocalTime.of(14, 7, 10)
        val localTime4 = LocalTime.of(1, 2, 5)

        // WHEN
        val result1 = localTime1.toDatabaseString()
        val result2 = localTime2.toDatabaseString()
        val result3 = localTime3.toDatabaseString()
        val result4 = localTime4.toDatabaseString()

        // THEN
        assertThat(result1).isEqualTo("14:27")
        assertThat(result2).isEqualTo("01:27")
        assertThat(result3).isEqualTo("14:07")
        assertThat(result4).isEqualTo("01:02")
    }
}