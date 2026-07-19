package com.tw.babybonus.ica.domain

import com.tw.babybonus.shared.Citizenship
import java.time.LocalDate

//Child is an External Data Model from ICA, so it is read-only
data class Child(
    val nric: String,

    val name: String,

    val dateOfBirth: LocalDate,

    val citizenship: Citizenship

) {

}