package com.tw.babybonus.enrollment.dto.response

import com.tw.babybonus.disbursement.dto.DisbursementResponse
import com.tw.babybonus.enrollment.domain.EnrollmentStatus
import java.util.UUID
import java.time.Instant

data class EnrollmentGetResponse(

    val id: UUID,

    val childNric: String,

    val status: EnrollmentStatus,

    val enrolledAt: Instant?,

    val disbursement: DisbursementResponse?

) {

}