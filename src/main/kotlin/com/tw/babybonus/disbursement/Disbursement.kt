package com.tw.babybonus.disbursement

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID
import kotlin.time.Instant

@Entity
@Table(name = "disbursements")
class Disbursement(

    @Id
    var id: UUID,

    var enrollmentID: UUID,

    var type: DisbursementType,

    var amount: BigDecimal,

    var status: DisbursementStatus,

    var processedAt: Instant?

) {

}