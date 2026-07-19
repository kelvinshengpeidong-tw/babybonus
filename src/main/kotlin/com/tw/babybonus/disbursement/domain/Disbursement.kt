package com.tw.babybonus.disbursement.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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

    @Enumerated(EnumType.STRING)
    val type: DisbursementType,

    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    var status: DisbursementStatus,

    var processedAt: Instant?

) {

}