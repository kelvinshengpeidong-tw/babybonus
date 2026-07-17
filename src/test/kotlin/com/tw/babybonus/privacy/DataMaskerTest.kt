package com.tw.babybonus.privacy

import com.tw.babybonus.exception.InvalidNricException
import com.tw.babybonus.validator.NricValidator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class DataMaskerTest {

    private val nricValidator = NricValidator()
    private val dataMasker = DataMasker(nricValidator)

    @Test
    fun `should mask valid NRIC`() {
        val nric = "S1234567D"
        val expectedMaskedNric = "S123****D"

        assertEquals(expectedMaskedNric, dataMasker.maskNric(nric));
    }

    @Test
    fun `should mask NRIC even if it contains lowercase letters`() {
        val nric = "s1234567d"
        val expectedMaskedNric = "S123****D"

        assertEquals(expectedMaskedNric, dataMasker.maskNric(nric));
    }

    @Test
    fun `should mask NRIC even if it contains leading or trailing spaces`() {
        val nric = "   S1234567D  "
        val expectedMaskedNric = "S123****D"

        assertEquals(expectedMaskedNric, dataMasker.maskNric(nric));
    }

    @Test
    //rest of the error cases have been validated in NricValidatorTests class
    fun `should throw exception when NRIC length is invalid`() {
        val nric = "S123456D"

        assertThrows<InvalidNricException> {
            dataMasker.maskNric(nric)
        }
    }
}