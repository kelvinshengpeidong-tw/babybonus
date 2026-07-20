package com.tw.babybonus.validator

import com.tw.babybonus.exception.InvalidNricException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class NricValidatorTest {

    val normalizedValidNric: String = "S1234567D"

    @Nested
    inner class NricFormat {

        @Test
        fun `should return normalized NRIC if NRIC format is valid`() {
            val nric = "S1234567D"
            assertEquals(normalizedValidNric, NricValidator.formatAndValidateNric(nric))
        }

        @Test
        fun `should return normalized NRIC when NRIC is lowercase`() {
            val nric = "s1234567d"
            assertEquals(normalizedValidNric, NricValidator.formatAndValidateNric(nric))
        }

        @Test
        fun `should return normalized NRIC when NRIC has leading and trailing spaces`() {
            val nric = "    S1234567D  "
            assertEquals(normalizedValidNric, NricValidator.formatAndValidateNric(nric))
        }

        @Test
        fun `should throw exception when NRIC length is invalid`() {
            val nric = "S12345D"
            assertThrows<InvalidNricException>{
                NricValidator.formatAndValidateNric(nric)
            }
        }

        @Test
        fun `should throw exception when NRIC starts with a digit`() {
            val nric = "91234567D"
            assertThrows<InvalidNricException>{
                NricValidator.formatAndValidateNric(nric)
            }
        }

        @Test
        fun `should throw exception when NRIC ends with a digit`() {
            val nric = "S12345679"
            assertThrows<InvalidNricException>{
                NricValidator.formatAndValidateNric(nric)
            }
        }

        @Test
        fun `should throw exception when NRIC contains non-digit characters in the middle`() {
            val nric = "S12AR567D"
            assertThrows<InvalidNricException>{
                NricValidator.formatAndValidateNric(nric)
            }
        }

        @Test
        fun `should throw exception when NRIC contains spaces in the middle`() {
            val nric = "S123 567D"
            assertThrows<InvalidNricException>{
                NricValidator.formatAndValidateNric(nric)
            }
        }

    }

}