package com.tw.babybonus.child

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "children")
class Child(

    @Id
    @JsonIgnore
    var nric: String,

    var name: String,

    var dateOfBirth: LocalDate,

    var citizenship: ChildCitizenship

) {

}