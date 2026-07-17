package com.tw.babybonus.validator

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class NricValidatorTests {

    val nricValidator = NricValidator()

    @Test
    fun `should return true if NRIC format is valid`() {
        val nric = "S1234567D"
        assertTrue(nricValidator.isNricFormatValid(nric))
    }

    @Test
    fun `should return true when NRIC is lowercase`() {
        val nric = "s1234567d"
        assertTrue(nricValidator.isNricFormatValid(nric))
    }

    @Test
    fun `should return true when NRIC has leading and trailing spaces`() {
        val nric = "    S1234567D  "
        assertTrue(nricValidator.isNricFormatValid(nric))
    }

    @Test
    fun `should return false when NRIC length is invalid`() {
        val nric = "S12345D"
        assertFalse(nricValidator.isNricFormatValid(nric))
    }

    @Test
    fun `should return false when NRIC starts with a digit`() {
        val nric = "91234567D"
        assertFalse(nricValidator.isNricFormatValid(nric))
    }

    @Test
    fun `should return false when NRIC ends with a digit`() {
        val nric = "S12345679"
        assertFalse(nricValidator.isNricFormatValid(nric))
    }

    @Test
    fun `should return false when NRIC contains non-digit characters in the middle`() {
        val nric = "S12AR567D"
        assertFalse(nricValidator.isNricFormatValid(nric))
    }

    @Test
    fun `should return false when NRIC contains spaces in the middle`() {
        val nric = "S123 567D"
        assertFalse(nricValidator.isNricFormatValid(nric))
    }
}