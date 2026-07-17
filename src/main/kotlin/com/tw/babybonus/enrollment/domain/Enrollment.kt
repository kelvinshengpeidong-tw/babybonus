package com.tw.babybonus.enrollment.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID
import kotlin.time.Instant

@Entity
@Table(name = "enrollments")
class Enrollment(

    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "child_nric")
    val childNric: String,

    @Column(name = "parent_nric")
    val parentNric: String,

    var status: EnrollmentStatus,

    var enrolledAt: Instant?,

    val createdAt: Instant

) {

}