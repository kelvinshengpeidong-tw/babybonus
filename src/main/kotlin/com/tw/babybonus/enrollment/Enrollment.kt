package com.tw.babybonus.enrollment

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID
import kotlin.time.Instant

@Entity
@Table(name = "enrollments")
class Enrollment(

    @Id
    var id: UUID,

    var childNric: String,

    var parentNric: String,

    var status: EnrollmentStatus,

    var enrolledAt: Instant?,

    var createdAt: Instant

) {

}