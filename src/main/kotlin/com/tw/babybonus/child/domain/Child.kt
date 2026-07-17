package com.tw.babybonus.child.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.tw.babybonus.shared.Citizenship
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "children")
class Child(

    @Id
    @JsonIgnore
    val nric: String,

    val name: String,

    val dateOfBirth: LocalDate,

    var citizenship: Citizenship

) {

}