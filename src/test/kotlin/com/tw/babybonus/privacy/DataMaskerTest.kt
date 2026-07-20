package com.tw.babybonus.privacy

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DataMaskerTest {

    @Nested
    inner class MaskNric {

        @Test
        fun `should mask valid NRIC`() {
            val nric = "S1234567D"
            val expectedMaskedNric = "S123****D"

            assertEquals(expectedMaskedNric, DataMasker.maskNric(nric))
        }

    }
}