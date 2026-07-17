package com.tw.babybonus.child.domain

import org.springframework.data.jpa.repository.JpaRepository

interface ChildRepository : JpaRepository<Child, String> {

}