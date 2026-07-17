package com.tw.babybonus.parent

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ParentRepository : JpaRepository<Parent, String> {

}