package com.tw.babybonus.enrollment.repository

import com.tw.babybonus.enrollment.domain.Enrollment
import com.tw.babybonus.enrollment.domain.EnrollmentStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface EnrollmentRepository : JpaRepository<Enrollment, UUID> {

    //let Spring JPA implement this method based on its name
    fun existsByChildNricAndStatusIn(childNric: String, status: Collection<EnrollmentStatus>): Boolean

}