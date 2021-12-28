package com.corrot.kwiatonomousapp.common

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertEquals
import org.junit.Test

class ExtensionsKtTest {
    @Test
    fun floatMapBetween_isCorrect() {
        // GIVEN
        val value = 0.252f
        val inMin = 0.0f
        val inMax = 1.0f
        val outMin = 0f
        val outMax = 1000f

        // WHEN
        val mappedResult = value.mapBetween(
            inMin = inMin, inMax = inMax,
            outMin = outMin, outMax = outMax
        )

        // THEN
        assertEquals(mappedResult, 252.0f)
    }

    @Test
    fun intToBooleanTrue() {
        // GIVEN
        val intValue = 1

        // WHEN
        val result = intValue.toBoolean()

        // THEN
        assertThat(result).isTrue()
    }

    @Test
    fun intToBooleanFalse() {
        // GIVEN
        val intValue = 0

        // WHEN
        val result = intValue.toBoolean()

        // THEN
        assertThat(result).isFalse()
    }
}