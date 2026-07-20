package com.tw.babybonus.validator

import com.tw.babybonus.exception.InvalidNricException

object NricValidator {

    fun formatAndValidateNric(nric: String): String {
        //remove any white spaces before/after the NRIC and convert to uppercase
        val normalizedNric = nric.trim().uppercase()

        //check if NRIC is valid
        //1. length = 9
        //2. first and last char are letters
        //3. rest of the 7 chars are digits
        if(!normalizedNric.matches(Regex("^[A-Z]\\d{7}[A-Z]$"))) {
            throw InvalidNricException("NRIC format is invalid!")
        }

        return normalizedNric
    }

}