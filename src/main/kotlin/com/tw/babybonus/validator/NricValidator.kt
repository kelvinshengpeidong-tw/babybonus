package com.tw.babybonus.validator

class NricValidator {

    fun isNricFormatValid(nric: String): Boolean {
        //remove any white spaces before/after the NRIC and convert to uppercase
        val normalizedNric = nric.trim().uppercase()

        //check if NRIC is valid
        //1. length = 9
        //2. first and last char are letters
        //3. rest of the 7 chars are digits
        return normalizedNric.matches(Regex("^[A-Z]\\d{7}[A-Z]$"))
    }

}