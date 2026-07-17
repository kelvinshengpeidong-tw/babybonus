package com.tw.babybonus.parent

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "parents")
class Parent(

    @Id
    @JsonIgnore
    var nric: String,

    var name: String,

    var relationship: ParentRelationship

) {

}