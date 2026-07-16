package com.tw.babybonus.disbursement

import java.math.BigDecimal
import java.util.UUID
import kotlin.time.Instant

class Disbursement constructor(val id: UUID, val enrollmentID: UUID, val type: DisbursementType, val amount: BigDecimal, val status: DisbursementStatus, val processedAt: Instant?) {

}