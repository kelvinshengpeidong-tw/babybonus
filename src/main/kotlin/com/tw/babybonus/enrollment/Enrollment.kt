package com.tw.babybonus.enrollment

import java.util.UUID
import kotlin.time.Instant

class Enrollment constructor(val id: UUID, val childNric: String, val parentNric: String, var status: EnrollmentStatus, val enrolledAt: Instant?, val createdAt: Instant) {

}