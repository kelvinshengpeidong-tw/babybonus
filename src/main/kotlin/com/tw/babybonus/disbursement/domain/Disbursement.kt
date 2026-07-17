package com.tw.babybonus.disbursement.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID
import kotlin.time.Instant

@Entity
@Table(name = "disbursements")
class Disbursement(

    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "enrollment_id")
    val enrollmentID: UUID,

    val type: DisbursementType,

    val amount: BigDecimal,

    var status: DisbursementStatus,

    var processedAt: Instant?

) {

}