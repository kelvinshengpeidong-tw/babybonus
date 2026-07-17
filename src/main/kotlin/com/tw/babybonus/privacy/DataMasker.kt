package com.tw.babybonus.privacy

import com.tw.babybonus.exception.InvalidNricException
import com.tw.babybonus.validator.NricValidator

class DataMasker(
    private val nricValidator: NricValidator
) {

    fun maskNric(nric: String): String {
        val normalizedNric = nric.trim().uppercase()

        //check validity of NRIC format
        if(!nricValidator.isNricFormatValid(normalizedNric)) {
            throw InvalidNricException("NRIC format is invalid!")
        }

        //masked the 4 chars in between
        return normalizedNric.substring(0, 4) + "****" + normalizedNric.last()
    }
}