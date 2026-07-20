package com.tw.babybonus.privacy

object DataMasker {

    //input is assumed to have been normalized and validated
    fun maskNric(normalizedNric: String): String {
        //masked the 4 chars in between
        return normalizedNric.substring(0, 4) + "****" + normalizedNric.last()
    }
}