package com.tw.babybonus.parent.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "parents")
class Parent(

    @Id
    @JsonIgnore
    val nric: String,

    val name: String,

    val relationship: ParentRelationship

) {

}