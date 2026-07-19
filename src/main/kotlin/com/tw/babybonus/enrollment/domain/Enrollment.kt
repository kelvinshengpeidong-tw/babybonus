package com.tw.babybonus.enrollment.domain

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID
import java.time.Instant

@Entity
@Table(name = "enrollments")
class Enrollment(

    @Id
    val id: UUID = UUID.randomUUID(),

    val childNric: String,

    val parentNric: String,

    @Enumerated(EnumType.STRING)
    var status: EnrollmentStatus,

    var enrolledAt: Instant?,

    val createdAt: Instant

) {

}