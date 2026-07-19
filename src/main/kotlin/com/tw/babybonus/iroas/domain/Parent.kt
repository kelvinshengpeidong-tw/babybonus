package com.tw.babybonus.iroas.domain

//Child is an External Data Model from ICA, so it is read-only
class Parent(

    val nric: String,

    val name: String,

    val relationship: ParentRelationship

) {

}